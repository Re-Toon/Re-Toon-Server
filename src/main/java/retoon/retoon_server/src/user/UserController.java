package retoon.retoon_server.src.user;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.user.entity.User;
import retoon.retoon_server.src.user.information.GetSocialUserRes;
import retoon.retoon_server.src.user.information.PostSocialUserRes;
import retoon.retoon_server.src.user.model.PatchUserReq;
import retoon.retoon_server.src.user.model.PostJoinUserReq;
import retoon.retoon_server.src.user.model.PostJoinUserRes;
import retoon.retoon_server.src.user.model.PostUserReq;
import retoon.retoon_server.src.user.repository.UserRepository;
import retoon.retoon_server.src.user.social.SocialLoginType;
import retoon.retoon_server.utils.JwtService;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtService jwtService;

    /**
     * GET / Social Login 페이지 이동 API
     * parameter Social Login Type(GOOGLE, NAVER, KAKAO)
     * */

    @GetMapping(value = "login/{socialLoginType}")
    public void socialLoginType(@PathVariable(name = "socialLoginType")
                                SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        userService.request(socialLoginType);
    }

    /**
     * GET / 로그인 및 회원가입 API
     * 자동적으로 redirect url 이동
     * Social Login API Server 요청에 의한 callback 처리
     * parameter socialLoginType (GOOGLE, NAVER, KAKAO)
     * */
    @GetMapping(value = "login/{socialLoginType}/callback")
    public BaseResponse<PostSocialUserRes> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                                    @RequestParam(name = "code") String code) throws JsonProcessingException {
        //인가 코드 획득
        log.info(">> SNS 로그인 서버에서 받은 code :: {}", code);

        //엑세스 토큰 획득
        String accessToken;
        accessToken = userService.requestAccessToken(socialLoginType, code);
        log.info(">> SNS 로그인 서버에서 받은 access token :: {}", accessToken);

        //사용자 정보 획득
        GetSocialUserRes socialUserRes;
        socialUserRes = userService.getUserInfo(socialLoginType, accessToken);

        //이메일 획득
        String email = socialUserRes.getEmail();
        log.info(">> SNS 로그인 서버에서 받은 사용자 이메일 :: {}", email);

        log.info(">> 유저 정보가 존재하는지 확인 :: {}", userService.isJoinedUser(email));
        //유저 정보가 존재하지 않으면 엑세스 토큰을 담아 회원가입
        if(!userService.isJoinedUser(email)){
            userService.SignUp(socialUserRes, accessToken); //DB에 정보 저장
        }
        //가입된 유저는 유저 정보가 담긴 JWT 토큰 발급
        User user = userRepository.findByEmail(email);
        log.info(">> 유저 이메일에 해당하는 유저 인덱스를 반환 :: {}", user.getUserIdx());
        String jwtToken = jwtService.createJwt(user.getUserIdx());
        log.info(">> SNS 로그인 서버에서 받은 사용자 jwt :: {}", jwtToken);

        //변경된 JWT 토큰 반영하는 부분
        userService.saveJwtToken(user, jwtToken);

        //클라이언트로 보낼 정보 반환
        PostSocialUserRes postSocialUserRes = new PostSocialUserRes(user.getUserIdx(), user.getName(), user.getEmail(), jwtToken);
        return new BaseResponse<>(postSocialUserRes);
    }

    @GetMapping(value = "/{socialLoginType}/logout")
    public String callbackLogout(@PathVariable(name = "socialLoginType")
                                 SocialLoginType socialLoginType){
        log.info(">> 사용자로부터 SNS 로그아웃 요청을 받음 :: {} Social Logout", socialLoginType);
        userService.requestlogout(socialLoginType);
        log.info(">> 로그아웃에 대한 응답 전송");
        return "로그아웃 성공";
    }

    /**
     * POST / 회원가입 API
     * parameter email, password
     * return String
     * */
    @PostMapping(value="/join")
    public BaseResponse<PostJoinUserRes> joinUser(@RequestBody PostJoinUserReq postJoinUserReq) {
        try{
            PostJoinUserRes joinUser = userService.joinUser(postJoinUserReq);
            return new BaseResponse<>(joinUser);
        }
        catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * GET / 로그인 상태 확인 API
     * parameter userIdx
     * return String
     */
    @GetMapping("/{userIdx}")
    public BaseResponse<String> verifyUser(@PathVariable("userIdx") int userIdx) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            String result;
            if(userIdx != userIdxByJwt){
                throw new BaseException(BaseResponseStatus.NOT_LOGIN_USER);
            }
            result = "로그인된 사용자입니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



    /**
     * POST / 프로필 생성 API
     * parameter userIdx, postUserReq
     * return String
     * */

    @PostMapping("/{userIdx}")
    public BaseResponse<String> createProfile(@PathVariable("userIdx") int userIdx, @RequestBody PostUserReq postUserReq){
        try{
            userService.createProfile(userIdx, postUserReq); // 유저 프로필 생성
            String result = "프로필 정보 생성을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>((e.getStatus()));
        }
    }


    /**
     * PATCH / 프로필 수정 API
     * parameter userIdx, patchUserReq
     * return String
     * */

    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyProfile(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq){
        try{
            userService.modifyProfile(userIdx, patchUserReq); // 유저 프로필 수정
            String result = "프로필 정보 수정을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * PATCH / 회원 탈퇴 API
     * parameter userIdx
     * return String
     * */

    @PatchMapping("/{userIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx){
        try{
            userService.deleteUser(userIdx); // 회원 탈퇴 진행
            String result = "회원 탈퇴를 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}