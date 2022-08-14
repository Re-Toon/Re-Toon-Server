package retoon.retoon_server.src.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.src.user.entity.UserGenre;
import retoon.retoon_server.src.user.information.GetSocialUserRes;
import retoon.retoon_server.src.user.repository.UserRepository;
import retoon.retoon_server.src.user.social.GoogleOauth;
import retoon.retoon_server.src.user.social.KakaoOauth;
import retoon.retoon_server.src.user.social.NaverOauth;
import retoon.retoon_server.src.user.social.SocialLoginType;
import retoon.retoon_server.src.user.entity.User;
import retoon.retoon_server.src.user.model.PatchUserReq;
import retoon.retoon_server.src.user.model.PostUserReq;
import retoon.retoon_server.src.user.entity.Genre;
import retoon.retoon_server.src.user.entity.UserProfile;
import retoon.retoon_server.src.user.repository.UserProfileRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    // 구글, 카카오, 네이버 oauth 객체 생성
    @Autowired
    private final GoogleOauth googleOauth;
    @Autowired
    private final KakaoOauth kakaoOauth;
    @Autowired
    private final NaverOauth naverOauth;

    private final HttpServletResponse response;
    @Autowired
    private final UserProfileRepository userProfileRepository;
    @Autowired
    private final UserRepository userRepository;

    //enum type 인식
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

    public String requestAccessToken(SocialLoginType socialLoginType, String code) throws JsonProcessingException {
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

    public boolean isJoinedUser(String email){
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        //사용자가 존재하는지의 여부를 반환
        return user.isPresent();
    }

    public void SignUp(GetSocialUserRes socialUserRes, String accessToken){
        User user = socialUserRes.toUser(accessToken);
        userRepository.save(user);
        userRepository.flush();
    }

    public void createProfile(int userIdx, PostUserReq postUserReq) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> userProfile = Optional.ofNullable(userRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 시 수정할 객체
        User makeProfile;

        if(userProfile.isPresent()){
            makeProfile = userProfile.get();
            makeProfile.setNickname(postUserReq.getNickname()); // 닉네임 설정
            makeProfile.setIntroduce(postUserReq.getIntroduce()); // 자기소개 설정
            makeProfile.setImgUrl(postUserReq.getImgUrl()); // 이미지 설정

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
    }

    // 프로필 수정하는 함수
    public void modifyProfile(int userIdx, PatchUserReq patchUserReq) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> userProfile = Optional.ofNullable(userRepository.findByUserIdx(userIdx));
        // 유저 정보 존재 시 수정할 객체
        User newProfile;
        // 유저 정보가 존재하는 경우
        if(userProfile.isPresent()){
            newProfile = userProfile.get();
            newProfile.setNickname(patchUserReq.getNickname()); // 닉네임 수정
            newProfile.setIntroduce(patchUserReq.getIntroduce()); // 자기소개 수정
            newProfile.setImgUrl(patchUserReq.getImgUrl()); // 이미지 수정

            // 장르 리스트 수정
            for(int i = 0; i < patchUserReq.getGenres().size(); i++){
                UserGenre genre = newProfile.getGenres().get(i); // 장르 객체 반환
                genre.setGenreName(patchUserReq.getGenres().get(i).getGenreName()); // 장르 객체 이름 변환
            }

            userRepository.save(newProfile);
            userRepository.flush(); // DB 반영
        }
    }

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
    }

}
