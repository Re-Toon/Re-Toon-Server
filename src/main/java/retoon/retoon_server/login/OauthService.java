package retoon.retoon_server.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.login.model.GetSocialUserRes;
import retoon.retoon_server.login.repository.OauthUser;
import retoon.retoon_server.login.repository.OauthUserRepository;
import retoon.retoon_server.login.social.SocialLoginType;
import retoon.retoon_server.login.social.GoogleOauth;
import retoon.retoon_server.login.social.NaverOauth;
import retoon.retoon_server.login.social.KakaoOauth;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OauthService {
    //구글, 카카오, 네이버 oauth 객체 생성
    @Autowired
    private final GoogleOauth googleOauth;
    @Autowired
    private final KakaoOauth kakaoOauth;
    @Autowired
    private final NaverOauth naverOauth;
    private final HttpServletResponse response;

    //Repository를 초기화하는 과정이 필요
    @Autowired
    private final OauthUserRepository oauthUserRepository;

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

    public String requestAccessToken(SocialLoginType socialLoginType, String code) throws JsonProcessingException{
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
        Optional<OauthUser> user = Optional.ofNullable(oauthUserRepository.findByEmail(email));
        //사용자가 존재하는지의 여부를 반환
        return user.isPresent();
    }

    public void SignUp(GetSocialUserRes socialUserRes, String accessToken){
        OauthUser user = socialUserRes.toUser(accessToken);
        oauthUserRepository.save(user);
        oauthUserRepository.flush();
    }

}
