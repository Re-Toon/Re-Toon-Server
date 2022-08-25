package retoon.retoon_server.src.user.service;

import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.user.entity.Follow;
import retoon.retoon_server.src.user.entity.User;
import retoon.retoon_server.src.user.entity.UserGenre;
import retoon.retoon_server.src.user.model.FollowResDto;
import retoon.retoon_server.src.user.model.FollowUserResDto;
import retoon.retoon_server.src.user.model.FollowListObjResDto;
import retoon.retoon_server.src.user.model.UserProfileDto;
import retoon.retoon_server.src.user.repository.FollowRepository;
import retoon.retoon_server.src.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Autowired 역할
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    @PersistenceContext
    private EntityManager entityManager; // entity 관리, 스프링에서 주입받기 위해 작성

    /** 프로필 생성 */
    public void createProfile(int userIdx, UserProfileDto profileDto) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> userProfile = userRepository.findByUserIdx(userIdx);
        // 유저 정보 존재 시 생성할 객체
        User makeProfile;
        // 올바른 프로필 정보 기입 확인
        checkProfile(profileDto);

        if(userProfile.isPresent()){ // 유저가 존재하는 경우
            makeProfile = userProfile.get();

            makeProfile.setNickname(profileDto.getNickname()); // 닉네임 설정
            makeProfile.setIntroduce(profileDto.getIntroduce()); // 자기소개 설정
            makeProfile.setImgUrl(profileDto.getImgUrl()); // 이미지 설정

            // 반복적으로 장르 리스트를 삽입
            for(int i = 0; i < profileDto.getGenres().size(); i++){
                UserGenre genre = new UserGenre(); // 장르 객체 생성
                genre.setGenreName(profileDto.getGenres().get(i).getGenreName()); // 장르 객체의 각 목록 이름 설정
                makeProfile.addGenre(genre); // 유저의 장르 리스트에 장르 객체 삽입
            }

            // DB 반영
            userRepository.saveAndFlush(makeProfile);
        }
        else{ throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); } // 유저가 존재하지 않는 경우
    }

    /** 프로필 올바른 기입 확인 */
    public void checkProfile(UserProfileDto profileDto) throws BaseException {
        // 닉네임을 입력하지 않은 경우
        if(profileDto.getNickname() == null || Objects.equals(profileDto.getNickname(), "")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_NICKNAME);
        }

        // 기존에 존재하는 닉네임인 경우
        if(userRepository.existsByNickname(profileDto.getNickname())){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
        }

        // 선호하는 장르 리스트가 4개가 아닌 경우
        if(profileDto.getGenres().size() != 4){
            throw new BaseException(BaseResponseStatus.INSUFFICIENT_USER_GENRE_LIST);
        }
    }

    /** 프로필 수정 */
    public void modifyProfile(int userIdx, UserProfileDto profileDto) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> userProfile = userRepository.findByUserIdx(userIdx);
        // 유저 정보 존재 시 수정할 객체
        User newProfile;
        // 올바른 프로필 정보 기입 확인
        checkProfile(profileDto);

        // 유저 정보가 존재하는 경우
        if (userProfile.isPresent()) {
            newProfile = userProfile.get();

            newProfile.setNickname(profileDto.getNickname()); // 닉네임 수정
            newProfile.setIntroduce(profileDto.getIntroduce()); // 자기소개 수정
            newProfile.setImgUrl(profileDto.getImgUrl()); // 이미지 수정

            // 장르 리스트 수정
            for (int i = 0; i < profileDto.getGenres().size(); i++) {
                UserGenre genre = newProfile.getGenres().get(i); // 장르 객체 반환
                genre.setGenreName(profileDto.getGenres().get(i).getGenreName()); // 장르 객체 이름 변환
            }

            // DB 반영
            userRepository.saveAndFlush(newProfile);
        } else {
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS);
        }
    }

    /** 팔로우 상태 확인 */
    @Transactional
    public int getFollowIdxByFromToUserIdx(int fromUserIdx, int toUserIdx) throws BaseException {
        Optional<User> follower = userRepository.findByUserIdx(fromUserIdx);
        Optional<User> followee = userRepository.findByUserIdx(toUserIdx);

        User fromUser; User toUser; // 유저 객체 생성
        if(follower.isPresent()){ fromUser = userRepository.findByUserIdx(fromUserIdx).get(); } // 팔로우 하는 사용자
        else{ throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        if(followee.isPresent()) { toUser = userRepository.findByUserIdx(toUserIdx).get(); } // 팔로우 당하는 사용자
        else{ throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        Follow follow = followRepository.findFollowByFromUserAndToUser(fromUser, toUser); // 팔로우 여부 확인

        if(follow != null) return follow.getFollowIdx(); // 팔로우 활성화 상태일 경우
        else return -1; // 팔로우 비활성화 상태일 경우
    }

    /** 팔로우 활성화, 팔로우 관계 저장 */
    @Transactional
    public FollowResDto followUser(int fromUserIdx, int toUserIdx) throws BaseException {
        Optional<User> fromUser = userRepository.findByUserIdx(fromUserIdx);
        Optional<User> toUser = userRepository.findByUserIdx(toUserIdx);

        if(fromUser.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }
        if(toUser.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        Follow isFollow = followRepository.findFollowByFromUserAndToUser(fromUser.get(), toUser.get()); // 팔로우 여부 확인
        if(isFollow != null) { throw new BaseException(BaseResponseStatus.EXISTS_FOLLOW_INFO); } // 팔로우 정보가 이미 존재

        Follow follow = followRepository.save(Follow.builder()
                .fromUser(fromUser.get())
                .toUser(toUser.get())
                .build()); // 정보 저장


        User getFromUser = follow.getFromUser(); User getToUser = follow.getToUser(); // follower, followee 정보 반환
        FollowUserResDto getFollower = new FollowUserResDto(getFromUser.getUserIdx(), getFromUser.getImgUrl(), getFromUser.getNickname(), getFromUser.getIntroduce());
        FollowUserResDto getFollowee = new FollowUserResDto(getToUser.getUserIdx(), getToUser.getImgUrl(), getToUser.getNickname(), getToUser.getIntroduce());

        return new FollowResDto(follow.getFollowIdx(), getFollower, getFollowee); // 팔로우 객체 반환
    }

    /** 유저 팔로워 목록 조회 */
    public List<FollowListObjResDto> getFollowerListByUserIdx(int userIdx, String loginEmail) throws BaseException {
        Optional<User> visitedUser = userRepository.findByUserIdx(userIdx); // 방문한 페이지의 사용자
        if(visitedUser.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        Optional<User> user = userRepository.findByEmail(loginEmail);
        if(user.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        int loginIdx = user.get().getUserIdx();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT u.user_idx, u.nickname, u.img_url, ");
        stringBuffer.append("if ((SELECT 1 FROM follow WHERE from_user_idx = ? AND to_user_idx = u.user_idx), TRUE, FALSE) As followState, ");
        stringBuffer.append("if ((?=u.user_idx), TRUE, FALSE) As loginUser ");
        stringBuffer.append("FROM user u, follow f ");
        stringBuffer.append("WHERE u.user_idx = f.from_user_idx AND f.to_user_idx = ?");

        Query query = entityManager.createNativeQuery(stringBuffer.toString())
                .setParameter(1, loginIdx)
                .setParameter(2, loginIdx)
                .setParameter(3, userIdx);

        JpaResultMapper result = new JpaResultMapper();
        return result.list(query, FollowListObjResDto.class);
    }

    /** 유저 팔로잉 목록 조회 */
    public List<FollowListObjResDto> getFollowingListByUserIdx(int userIdx, String loginEmail) throws BaseException {
        Optional<User> visitedUser = userRepository.findByUserIdx(userIdx); // 방문한 페이지의 사용자
        if(visitedUser.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        Optional<User> user = userRepository.findByEmail(loginEmail);
        if(user.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        int loginIdx = user.get().getUserIdx();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT u.user_idx, u.nickname, u.img_url, ");
        stringBuffer.append("if ((SELECT 1 FROM follow WHERE from_user_idx = ? AND to_user_idx = u.user_idx), TRUE, FALSE) As followState,");
        stringBuffer.append("if ((?=u.user_idx), TRUE, FALSE) As loginUser ");
        stringBuffer.append("FROM user u, follow f ");
        stringBuffer.append("WHERE u.user_idx = f.to_user_idx AND f.from_user_idx = ?");

        Query query = entityManager.createNativeQuery(stringBuffer.toString())
                .setParameter(1, loginIdx)
                .setParameter(2, loginIdx)
                .setParameter(3, userIdx);

        JpaResultMapper result = new JpaResultMapper();
        return result.list(query, FollowListObjResDto.class);
    }

}
