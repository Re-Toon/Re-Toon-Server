package retoon.retoon_server.src.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.user.model.*;
import retoon.retoon_server.src.user.service.SignService;
import retoon.retoon_server.src.user.service.SocialService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {
    private final SignService signService;
    private final SocialService socialService;

    /**
     * POST / 회원가입 API
     * parameter UserRegisterRequestDto
     * return UserRegisterResponseDto
     * */
    @PostMapping("/register")
    public BaseResponse<UserRegisterResDto> register(@RequestBody UserRegisterReqDto requestDto) {
        try{
            UserRegisterResDto responseDto = signService.registerUser(requestDto);
            return new BaseResponse<>(responseDto);
        }
        catch(BaseException exception){ return new BaseResponse<>(exception.getStatus()); }
    }

    /**
     * POST / 이메일 본인인증 API
     * parameter EmailAuthRequestDto
     * return String
     * */
    @GetMapping("/confirm-email")
    public BaseResponse<String> confirmEmail(@RequestBody EmailAuthReqDto requestDto) {
        try{
            signService.confirmEmail(requestDto);
            return new BaseResponse<>("인증이 완료되었습니다.");
        }
        catch(BaseException exception){ return new BaseResponse<>(exception.getStatus()); }
    }

    /**
     * POST / 로그인 API
     * parameter UserLoginRequestDto
     * return UserLoginResponseDto
     * */

    @PostMapping("/login")
    public BaseResponse<UserLoginResDto> login(@RequestBody UserLoginReqDto requestDto){
        try{
            signService.checkLogin(requestDto);
            UserLoginResDto responseDto = signService.loginUser(requestDto);
            return new BaseResponse<>(responseDto);
        }
        catch(BaseException exception) { return new BaseResponse<>(exception.getStatus()); }
    }

    /**
     * POST / 토큰 재발급 API
     * parameter TokenRequestDto
     * return TokenResponseDto
     * */
    @PostMapping("/reissue")
    public BaseResponse<TokenResDto> reIssue(@RequestBody TokenReqDto tokenReqDto) {
        try{
            signService.checkToken(tokenReqDto); // 토큰 만료 확인
            TokenResDto responseDto = signService.reIssue(tokenReqDto); // 토큰 재발급 진행
            return new BaseResponse<>(responseDto);
        }
        catch(BaseException exception) { return new BaseResponse<>(exception.getStatus()); }
    }

    /**
     * GET / SNS 로그인 API
     * parameter x
     * return UserLoginResponseDto
     * */
    @GetMapping(value = "/login/{provider}")
    public void socialLoginType(@PathVariable(name = "provider")
                                String provider) {
        socialService.request(provider);
    }

    @GetMapping(value = "/login/{provider}/callback")
    public BaseResponse<UserLoginResDto> callback(@PathVariable(name = "provider") String provider,
                                                  @RequestParam(name = "code") String code) {
        UserLoginResDto responseDto = signService.loginUserByProvider(code, provider);
        return new BaseResponse<>(responseDto);
    }

    /**
     * PATCH / 회원 탈퇴 API
     * parameter userIdx
     * return String
     * */
    @PatchMapping("/status/{userIdx}")
    public BaseResponse<String> disabledUser(@PathVariable("userIdx") int userIdx) {
        try{
            signService.disabledUser(userIdx); // 회원 탈퇴 진행
            String result = "회원 탈퇴를 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * GET / 비밀번호 확인 API
     * parameter UserPasswordRequestDto
     * return string
     * */
    @GetMapping("/verify-password")
    public BaseResponse<String> verifyPwd(@RequestBody UserPasswordReqDto requestDto) {
        try{
            signService.checkEqualPwd(requestDto);
            String result = "비밀번호가 일치합니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * PATCH / 비밀번호 재설정 API
     * parameter UserPasswordResetReqDto
     * return String
     * */
    @PatchMapping("/reset-password")
    public BaseResponse<String> resetPwd(@RequestBody UserPasswordResetReqDto requestDto){
        try{
            signService.resetPwd(requestDto);
            String result = "비밀번호 재설정을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
