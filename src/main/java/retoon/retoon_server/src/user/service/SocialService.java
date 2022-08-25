package retoon.retoon_server.src.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import retoon.retoon_server.src.user.auth.oauth.OAuthGoogle;
import retoon.retoon_server.src.user.auth.oauth.OAuthKakao;
import retoon.retoon_server.src.user.auth.oauth.OAuthNaver;
import retoon.retoon_server.src.user.converter.ProviderConverter;
import retoon.retoon_server.src.user.entity.Provider;
import retoon.retoon_server.src.user.model.SocialProfileDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor // Autowired 역할
public class SocialService {
    // 구글, 카카오, 네이버 oauth 객체 생성
    private final OAuthGoogle oAuthGoogle;
    private final OAuthKakao oAuthKakao;
    private final OAuthNaver oAuthNaver;
    private final HttpServletResponse response;
    private final ProviderConverter providerConverter;

    /** SNS 로그인 리다이렉트 페이지 이동 */
    public void request(String provider){
        //redirect 처리를 할 url 생성
        String redirectURL;
        Provider LoginType = providerConverter.convert(provider);
        switch(LoginType){
            case GOOGLE:{
                redirectURL = oAuthGoogle.getRedirectURL();
            }break;
            case KAKAO:{
                redirectURL = oAuthKakao.getRedirectURL();
            }break;
            case NAVER:{
                redirectURL = oAuthNaver.getRedirectURL();
            }break;
            default:{
                throw new IllegalArgumentException("등록되지 않은 SNS 로그인 형식입니다.");
            }
        }
        try{
            //정해진 url sendRedirect 처리
            System.out.println(response.getStatus());
            response.sendRedirect(redirectURL); //로그인 페이지 이동 및 로그인 확인
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /** SNS 로그인 엑세스 토큰 요청 및 반환 */
    public String extractAccessToken(String provider, String code) {
        Provider LoginType = providerConverter.convert(provider);
        switch (LoginType){
            case GOOGLE:{
                try {
                    return oAuthGoogle.extractAccessToken(code);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case KAKAO:{
                try {
                    return oAuthKakao.extractAccessToken(code);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case NAVER:{
                try{
                    return oAuthNaver.extractAccessToken(code);
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
    public SocialProfileDto getUserProfileInfo(String provider, String accessToken){
        Provider LoginType = providerConverter.convert(provider);
        switch (LoginType){
            case GOOGLE:{
                return oAuthGoogle.getUserProfileInfo(accessToken);
            }
            case KAKAO:{
                return oAuthKakao.getUserProfileInfo(accessToken);
            }
            case NAVER:{
                return oAuthNaver.getUserProfileInfo(accessToken);
            }
            default:{
                // validation 처리로 변경
                throw new IllegalArgumentException("등록되지 않은 SNS 로그인 형식입니다.");
            }
        }
    }
}
