package retoon.retoon_server.src.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.src.user.model.PatchUserReq;
import retoon.retoon_server.src.user.model.PostUserReq;
import retoon.retoon_server.src.user.repository.Genre;
import retoon.retoon_server.src.user.repository.UserProfile;
import retoon.retoon_server.src.user.repository.UserProfileRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserProfileRepository userProfileRepository;

    public void createProfile(PostUserReq postUserReq) throws BaseException {
        UserProfile makeProfile = new UserProfile(); // 새로운 유저 프로필 객체 생성
        makeProfile.setNickname(postUserReq.getNickname()); // 닉네임 설정
        makeProfile.setIntroduce(postUserReq.getIntroduce()); // 자기소개 설정
        makeProfile.setImgUrl(postUserReq.getImgUrl()); //

        // 반복적으로 장르 리스트를 삽입
        for(int i = 0; i < postUserReq.getGenres().size(); i++){
            Genre genre = new Genre(); // 장르 객체 생성
            genre.setGenreName(postUserReq.getGenres().get(i).getGenreName()); // 장르 객체의 각 목록 이름 설정
            makeProfile.addGenre(genre); // 유저의 장르 리스트에 장르 객체 삽입
        }

        // 반영 전에 저장
        userProfileRepository.save(makeProfile);
        // DB 반영
        userProfileRepository.flush();
        // 생성된 유저 프로필 정보를 반환
        //return makeProfile;
    }

    // 프로필 수정하는 함수
    public void modifyProfile(int userIdx, PatchUserReq patchUserReq) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<UserProfile> userProfile = Optional.ofNullable(userProfileRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 시 수정할 객체
        UserProfile newProfile;
        // 유저 정보가 존재하는 경우
        if(userProfile.isPresent()){
            newProfile = userProfile.get();
            newProfile.setNickname(patchUserReq.getNickname()); // 닉네임 수정
            newProfile.setIntroduce(patchUserReq.getIntroduce()); // 자기소개 수정
            newProfile.setImgUrl(patchUserReq.getImgUrl()); // 이미지 수정

            // 장르 리스트 수정
            for(int i = 0; i < patchUserReq.getGenres().size(); i++){
                Genre genre = newProfile.getGenres().get(i); // 장르 객체 반환
                genre.setGenreName(patchUserReq.getGenres().get(i).getGenreName()); // 장르 객체 이름 변환
            }

            userProfileRepository.save(newProfile);
            userProfileRepository.flush(); // DB 반영
        }
        // 수정된 프로필 정보를 반환
        // return newProfile;
    }

    public void deleteProfile(int userIdx) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<UserProfile> userProfile = Optional.ofNullable(userProfileRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 여부 확인
        if(userProfile.isPresent()){
            UserProfile getProfile = userProfile.get(); // 유저 정보 획득
            getProfile.setStatus("INACTIVE"); // 유저 정보 수정
            userProfileRepository.save(getProfile); // 저장
            userProfileRepository.flush(); // DB 반영
        }
    }
}
