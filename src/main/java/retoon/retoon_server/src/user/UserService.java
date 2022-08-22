package retoon.retoon_server.src.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.user.entity.Follow;
import retoon.retoon_server.src.user.entity.UserGenre;
import retoon.retoon_server.src.user.information.GetSocialUserRes;
import retoon.retoon_server.src.user.model.*;
import retoon.retoon_server.src.user.model.mypage.GetUserFollowRes;
import retoon.retoon_server.src.user.model.mypage.GetUserProfileRes;
import retoon.retoon_server.src.user.repository.FollowRepository;
import retoon.retoon_server.src.user.repository.UserRepository;
import retoon.retoon_server.src.user.social.GoogleOauth;
import retoon.retoon_server.src.user.social.KakaoOauth;
import retoon.retoon_server.src.user.social.NaverOauth;
import retoon.retoon_server.src.user.social.SocialLoginType;
import retoon.retoon_server.src.user.entity.User;
import retoon.retoon_server.utils.JwtService;
import retoon.retoon_server.utils.SHA256;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static retoon.retoon_server.utils.ValidationRegex.*;

@Service
@RequiredArgsConstructor // Autowired 역할
public class UserService {

    // 구글, 카카오, 네이버 oauth 객체 생성
    private final GoogleOauth googleOauth;
    private final KakaoOauth kakaoOauth;
    private final NaverOauth naverOauth;
    private final HttpServletResponse response;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final FollowRepository followRepository;

    @PersistenceContext
    private EntityManager entityManager; // entity 관리, 스프링에서 주입받기 위해 작성

    /** SNS 로그인 리다이렉트 페이지 이동 */
    public void request(SocialLoginType socialLoginType){
        //redirect 처리를 할 url 생성
        String redirectURL;
        switch(socialLoginType){
            case GOOGLE:{
                redirectURL = googleOauth.getOauthRedirectURL();
            }break;
            case KAKAO:{
                redirectURL = kakaoOauth.getOauthRedirectURL();
            }break;
            case NAVER:{
                redirectURL = naverOauth.getOauthRedirectURL();
            }break;
            default:{
                throw new IllegalArgumentException("등록되지 않은 SNS 로그인 형식입니다.");
            }
        }
        try{
            //정해진 url로 sendRedirect 처리
            System.out.println(response.getStatus());
            response.sendRedirect(redirectURL); //로그인 페이지 이동 및 로그인 확인
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /** SNS 로그인 엑세스 토큰 요청 및 반환 */
    public String requestAccessToken(SocialLoginType socialLoginType, String code) {
        switch (socialLoginType){
            case GOOGLE:{
                try {
                    return googleOauth.requestAccessToken(code);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case KAKAO:{
                try {
                    return kakaoOauth.requestAccessToken(code);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case NAVER:{
                try{
                    return naverOauth.requestAccessToken(code);
                }catch (JsonProcessingException e){
                    throw new RuntimeException(e);
                }
            }
            default:{
                // validation 처리로 변경
                throw new IllegalArgumentException("등록되지 않은 SNS 로그인 형식입니다.");
            }
        }
    }

    /** SNS 로그인 사용자 정보 반환 */
    public GetSocialUserRes getUserInfo(SocialLoginType socialLoginType, String accessToken){
        switch (socialLoginType){
            case GOOGLE:{
                return googleOauth.getUserInfo(accessToken);
            }
            case KAKAO:{
                return kakaoOauth.getUserInfo(accessToken);
            }
            case NAVER:{
                return naverOauth.getUserInfo(accessToken);
            }
            default:{
                // validation 처리로 변경
                throw new IllegalArgumentException("등록되지 않은 SNS 로그인 형식입니다.");
            }
        }
    }

    /** SNS 로그인 로그아웃(아직 정확한 구현이 되지 않은 상태) */
    public void requestlogout(SocialLoginType socialLoginType){
        String logoutUrl;
        switch (socialLoginType){
            case GOOGLE:{
                logoutUrl = googleOauth.logout();
            }break;
            case KAKAO:{
                logoutUrl = kakaoOauth.logout();
            }break;
            case NAVER:{
                logoutUrl = naverOauth.logout();
            }break;
            default:{
                // validation 처리로 변경
                throw new IllegalArgumentException("등록되지 않은 SNS 로그인 형식입니다.");
            }
        }
        try{
            //정해진 url로 sendRedirect 처리
            response.sendRedirect(logoutUrl); //로그아웃 페이지 이동
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /** 사용자 존재 여부 확인 */
    public boolean isJoinedUser(String email){
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        //사용자가 존재하는지의 여부를 반환
        return user.isPresent();
    }

    /** SNS 로그인 회원가입 */
    public void SignUp(GetSocialUserRes socialUserRes, String accessToken){
        User user = socialUserRes.toUser(accessToken);
        userRepository.save(user);
        userRepository.flush();
    }

    /** JWT 토큰 저장 */
    public void saveJwtToken(User user, String jwtToken){
        //변경된 JWT 토큰 반영하는 부분
        user.setJwtToken(jwtToken); // 유저 객체의 jwt token 수정
        userRepository.save(user);
        userRepository.flush(); // DB 반영
    }

    /** 일반 회원가입 */
    public PostJoinUserRes joinUser(PostJoinUserReq postJoinUserReq) throws BaseException {
        // 사용자 이름을 입력하지 않은 경우
        if(postJoinUserReq.getName() == null || postJoinUserReq.getName().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_NAME);
        }

        // 사용자 이메일을 입력하지 않은 경우
        if (postJoinUserReq.getEmail() == null || postJoinUserReq.getEmail().equals("")) {
            throw new BaseException(BaseResponseStatus.EMPTY_USER_EMAIL);
        }

        // 기존에 존재하는 이메일인 경우
        if(userRepository.existsByEmail(postJoinUserReq.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }

        // 이메일 정규표현식이 아닌 경우
        if(!isRegexEmail(postJoinUserReq.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        // 사용자 비밀번호를 입력하지 않은 경우
        if(postJoinUserReq.getPassword() == null || postJoinUserReq.getPassword().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_PASSWORD);
        }

        // 비밀번호가 영문, 숫자, 특수문자를 섞어서 넣지 않은 경우
        if(!isRegexPassword(postJoinUserReq.getPassword())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
        }

        // 사용자 비밀번호 확인을 한번 더 입력하지 않은 경우
        if(postJoinUserReq.getPasswordCheck() == null || postJoinUserReq.getPasswordCheck().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_CHECK_PASSWORD);
        }

        // 사용자 비밀번호와 비밀번호 확인이 일치하지 않는 경우
        if(!postJoinUserReq.getPasswordCheck().equals(postJoinUserReq.getPassword())){
            throw new BaseException(BaseResponseStatus.NOT_EQUAL_PASSWORD);
        }

        // 모든 유효성 검증을 마친 경우
        User user = new User();
        user.setEmail(postJoinUserReq.getEmail());
        user.setName(postJoinUserReq.getName());
        // 비밀번호 암호화
        String encryptPwd;
        try{
            // 비밀번호 암호화
            new SHA256();
            encryptPwd = SHA256.encrypt(postJoinUserReq.getPassword());
        }
        catch(Exception e){
            // 비밀번호 암호화 실패
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        user.setPassword(encryptPwd); // 암호화된 비밀번호를 DB에 저장

        // DB 반영
        userRepository.save(user);
        userRepository.flush();

        User joinUser = userRepository.findByEmail(postJoinUserReq.getEmail());

        return new PostJoinUserRes(joinUser.getUserIdx(), joinUser.getName(), joinUser.getEmail());
    }

    /** 이메일 일반 로그인 */
    public PostLoginUserRes loginUser(PostLoginUserReq postLoginUserReq) throws BaseException {
        // 이메일로 사용자 조회
        if(!userRepository.existsByEmail(postLoginUserReq.getEmail())){
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS);
        }
        // 사용자가 존재하는 경우에 해당 객체를 반환
        User user = userRepository.findByEmail(postLoginUserReq.getEmail());
        String encryptPwd;
        try{
            // 비밀번호 암호화
            new SHA256();
            encryptPwd = SHA256.encrypt(postLoginUserReq.getPassword());
        }
        catch(Exception e){
            // 비밀번호 암호화 실패
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){ // 암호화된 비밀번호가 DB에 저장된 비밀번호와 일치하는 경우 JWT 토큰 발급
            int userIdx = user.getUserIdx();
            String jwtToken = jwtService.createJwt(userIdx);
            saveJwtToken(user, jwtToken); // JWT 토큰 저장
            return new PostLoginUserRes(userIdx, jwtToken);
        }
        else{
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }

    }

    /** 프로필 생성 */
    public void createProfile(int userIdx, PostUserReq postUserReq) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> userProfile = Optional.ofNullable(userRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 시 수정할 객체
        User makeProfile;

        if(userProfile.isPresent()){
            // 유저가 존재하는 경우
            makeProfile = userProfile.get();

            // 닉네임을 입력하지 않은 경우
            if(postUserReq.getNickname() == null || Objects.equals(postUserReq.getNickname(), "")){
                throw new BaseException(BaseResponseStatus.EMPTY_USER_NICKNAME);
            }

            // 기존에 존재하는 닉네임인 경우
            if(userRepository.existsByNickname(postUserReq.getNickname())){
                throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
            }

            makeProfile.setNickname(postUserReq.getNickname()); // 닉네임 설정
            makeProfile.setIntroduce(postUserReq.getIntroduce()); // 자기소개 설정
            makeProfile.setImgUrl(postUserReq.getImgUrl()); // 이미지 설정

            // 선호하는 장르 리스트가 4개가 아닌 경우
            if(postUserReq.getGenres().size() != 4){
                throw new BaseException(BaseResponseStatus.INSUFFICIENT_USER_GENRE_LIST);
            }

            // 반복적으로 장르 리스트를 삽입
            for(int i = 0; i < postUserReq.getGenres().size(); i++){
                UserGenre genre = new UserGenre(); // 장르 객체 생성
                genre.setGenreName(postUserReq.getGenres().get(i).getGenreName()); // 장르 객체의 각 목록 이름 설정
                makeProfile.addGenre(genre); // 유저의 장르 리스트에 장르 객체 삽입
            }

            // 반영 전에 저장
            userRepository.save(makeProfile);
            // DB 반영
            userRepository.flush();
        }
        else{
            // 유저가 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS);
        }
    }

    /** 프로필 수정 */
    public void modifyProfile(int userIdx, PatchUserReq patchUserReq) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> userProfile = Optional.ofNullable(userRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 시 수정할 객체
        User newProfile;
        // 유저 정보가 존재하는 경우
        if(userProfile.isPresent()){
            newProfile = userProfile.get();

            if(patchUserReq.getNickname() == null || Objects.equals(patchUserReq.getNickname(), "")){
                throw new BaseException(BaseResponseStatus.EMPTY_USER_NICKNAME);
            }

            // 기존에 존재하는 닉네임인 경우
            if(userRepository.existsByNickname(patchUserReq.getNickname())){
                throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
            }

            newProfile.setNickname(patchUserReq.getNickname()); // 닉네임 수정
            newProfile.setIntroduce(patchUserReq.getIntroduce()); // 자기소개 수정
            newProfile.setImgUrl(patchUserReq.getImgUrl()); // 이미지 수정

            if(patchUserReq.getGenres().size() != 4){
                throw new BaseException(BaseResponseStatus.INSUFFICIENT_USER_GENRE_LIST);
            }
            // 장르 리스트 수정
            for(int i = 0; i < patchUserReq.getGenres().size(); i++){
                UserGenre genre = newProfile.getGenres().get(i); // 장르 객체 반환
                genre.setGenreName(patchUserReq.getGenres().get(i).getGenreName()); // 장르 객체 이름 변환
            }

            userRepository.save(newProfile);
            userRepository.flush(); // DB 반영
        }
        else{
            // 유저가 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS);
        }
    }

    /** 회원 탙퇴 */
    public void deleteUser(int userIdx) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> user = Optional.ofNullable(userRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 여부 확인
        if(user.isPresent()){
            User getUser = user.get(); // 유저 정보 획득
            getUser.setStatus("INACTIVE"); // 유저 정보 수정
            userRepository.save(getUser); // 저장
            userRepository.flush(); // DB 반영
        }
        else{
            // 유저가 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS);
        }
    }

    /** 팔로우 상태 확인 */
    @Transactional
    public int getFollowIdxByFromToUserIdx(int fromUserIdx, int toUserIdx) throws BaseException {
        Optional<User> follower = Optional.ofNullable(userRepository.findByUserIdx(fromUserIdx));
        Optional<User> followee = Optional.ofNullable(userRepository.findByUserIdx(toUserIdx));

        User fromUser; User toUser; // 유저 객체 생성
        if(follower.isPresent()){ fromUser = userRepository.findByUserIdx(fromUserIdx); } // 팔로우 하는 사용자
        else{ throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        if(followee.isPresent()) { toUser = userRepository.findByUserIdx(toUserIdx); } // 팔로우 당하는 사용자
        else{ throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        Follow follow = followRepository.findFollowByFromUserAndToUser(fromUser, toUser); // 팔로우 여부 확인

        if(follow != null) return follow.getFollowIdx(); // 팔로우 활성화 상태일 경우
        else return -1; // 팔로우 비활성화 상태일 경우
    }

    /** 팔로우 활성화, 팔로우 관계 저장 */
    @Transactional
    public PostFollowRes followUser(int fromUserIdx, int toUserIdx) throws BaseException {
        User fromUser = userRepository.findByUserIdx(fromUserIdx);
        User toUser = userRepository.findByUserIdx(toUserIdx);

        Follow isFollow = followRepository.findFollowByFromUserAndToUser(fromUser, toUser); // 팔로우 여부 확인
        if(isFollow != null) { throw new BaseException(BaseResponseStatus.EXISTS_FOLLOW_INFO); } // 팔로우 정보가 이미 존재

        Follow follow = followRepository.save(Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build()); // 정보 저장

        User getFromUser = follow.getFromUser(); User getToUser = follow.getToUser(); // follower, followee 정보 반환
        PostFollowUserRes getFollower = new PostFollowUserRes(getFromUser.getUserIdx(), getFromUser.getImgUrl(), getFromUser.getNickname(), getFromUser.getIntroduce());
        PostFollowUserRes getFollowee = new PostFollowUserRes(getToUser.getUserIdx(), getToUser.getImgUrl(), getToUser.getNickname(), getToUser.getIntroduce());

        return new PostFollowRes(follow.getFollowIdx(), getFollower, getFollowee); // 팔로우 객체 반환
    }

    /** 마이페이지 프로필 부분, 팔로우 목록 반환 */
    public GetUserProfileRes getProfile(int currentIdx, String loginEmail){
        GetUserProfileRes getUserProfileRes = new GetUserProfileRes();

        User user = userRepository.findByUserIdx(currentIdx); // 현재 인덱스에 해당하는 유저 정보로 객체 생성
        getUserProfileRes.setNickname(user.getNickname()); // 닉네임 지정
        getUserProfileRes.setIntroduce(user.getIntroduce()); // 자기소개 지정
        getUserProfileRes.setProfileImgUrl(user.getImgUrl()); // 프로필 이미지 지정

        User loginUser = userRepository.findByEmail(loginEmail); // 로그인된 사용자 정보를 찾음
        getUserProfileRes.setLoginUser(loginUser.getUserIdx() == user.getUserIdx()); // 로그인된 사용자의 정보와 일치하는지를 확인
        getUserProfileRes.setLoginIdx(loginUser.getUserIdx()); // 로그인된 사용자의 정보를 삽입

        // currentIdx 가진 유저가 login email 가진 유저를 팔로우 했는지를 확인
        getUserProfileRes.setFollow(followRepository.findFollowByFromUserAndToUser(loginUser, user) != null);

        // currentIdx 가진 유저의 팔로워, 팔로잉 수를 확인
        getUserProfileRes.setFollowerCount(followRepository.findFollowerCountByFromUserIdx(currentIdx));
        getUserProfileRes.setFollowingCount(followRepository.findFollowingCountByToUserIdx(currentIdx));

        return getUserProfileRes;
    }

    /** 유저 팔로워 목록 조회 */
    public List<GetUserFollowRes> getFollowerListByUserIdx(int userIdx, String loginEmail){
        int loginIdx = userRepository.findByEmail(loginEmail).getUserIdx();

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
        return result.list(query, GetUserFollowRes.class);
    }

    /** 유저 팔로잉 목록 조회 */
    public List<GetUserFollowRes> getFollowingListByUserIdx(int userIdx, String loginEmail){
        int loginIdx = userRepository.findByEmail(loginEmail).getUserIdx();

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
        return result.list(query, GetUserFollowRes.class);
    }

}
