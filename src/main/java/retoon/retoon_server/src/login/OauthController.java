package retoon.retoon_server.src.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.login.model.GetSocialUserRes;
import retoon.retoon_server.src.login.model.PostSocialUserRes;
import retoon.retoon_server.src.login.repository.OauthUser;
import retoon.retoon_server.src.login.repository.OauthUserRepository;
import retoon.retoon_server.src.login.social.SocialLoginType;
import retoon.retoon_server.utils.JwtService;

@RestController //controller + ResponseBody
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Slf4j
public class OauthController {
    //OauthService 객체 생성
    @Autowired
    private final OauthService oauthService;
    //Repository 초기화 과정이 필요
    @Autowired
    private final OauthUserRepository oauthUserRepository;
    @Autowired
    private final JwtService jwtService;
    /**
     * 사용자로부터 SNS 로그인 요청을 Social Login Type 으로 받아 처리
     * parameter socialLoginType (GOOGLE, NAVER, KAKAO)
     * */

    @GetMapping(value = "/{socialLoginType}")
    public void socialLoginType(@PathVariable(name = "socialLoginType")
                                SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        oauthService.request(socialLoginType);
    }

    /**
     * Social Login API Server 요청에 의한 callback 처리
     * parameter socialLoginType (GOOGLE, NAVER, KAKAO)
     * return SNS 로그인 요청 결과로 받은 JSON 형태의 문자열, 사용자 정보
     * access_token, refresh_token + 사용자 정보
     * 사용자 정보를 얻어서 DB에 저장
     * */
    @GetMapping(value = "/{socialLoginType}/callback")
    public BaseResponse<PostSocialUserRes> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                                    @RequestParam(name = "code") String code) throws JsonProcessingException {
        //인가 코드 획득
        log.info(">> SNS 로그인 서버에서 받은 code :: {}", code);

        //엑세스 토큰 획득
        String accessToken = "";
        accessToken = oauthService.requestAccessToken(socialLoginType, code);
        log.info(">> SNS 로그인 서버에서 받은 access token :: {}", accessToken);

        //사용자 정보 획득
        GetSocialUserRes socialUserRes;
        socialUserRes = oauthService.getUserInfo(socialLoginType, accessToken);

        //이메일 획득
        String email = socialUserRes.getEmail();
        log.info(">> SNS 로그인 서버에서 받은 사용자 이메일 :: {}", email);

        log.info(">> 유저 정보가 존재하는지 확인 :: {}", oauthService.isJoinedUser(email));
        //유저 정보가 존재하지 않으면 엑세스 토큰을 담아 회원가입
        if(!oauthService.isJoinedUser(email)){
            oauthService.SignUp(socialUserRes, accessToken); //DB에 정보 저장
        }
        //가입된 유저는 유저 정보가 담긴 JWT 토큰 발급
        OauthUser oauthUser = oauthUserRepository.findByEmail(email);
        log.info(">> 유저 이메일에 해당하는 유저 인덱스를 반환 :: {}", oauthUser.getUserIdx());
        String jwtToken = jwtService.createJwt(oauthUser.getUserIdx());
        log.info(">> SNS 로그인 서버에서 받은 사용자 jwt :: {}", jwtToken);

        //클라이언트로 보낼 정보 반환
        PostSocialUserRes postSocialUserRes = new PostSocialUserRes(oauthUser.getUserIdx(), oauthUser.getName(), oauthUser.getEmail(), jwtToken);
        return new BaseResponse<>(postSocialUserRes);
    }

    @GetMapping(value = "/{socialLoginType}/logout")
    public String callbackLogout(@PathVariable(name = "socialLoginType")
                             SocialLoginType socialLoginType){
        log.info(">> 사용자로부터 SNS 로그아웃 요청을 받음 :: {} Social Logout", socialLoginType);
        oauthService.requestlogout(socialLoginType);
        log.info(">> 로그아웃에 대한 응답 전송");
        return "로그아웃 성공";
    }

}

