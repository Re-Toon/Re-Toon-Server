package retoon.retoon_server.src.login.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.login.model.*;
import retoon.retoon_server.src.login.service.SignService;
import retoon.retoon_server.src.login.service.SocialService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {
    private final SignService signService;
    private final SocialService socialService;

    @PostMapping("/register")
    public BaseResponse<MemberRegisterResponseDto> register(@RequestBody MemberRegisterRequestDto requestDto) throws Exception {
        MemberRegisterResponseDto responseDto = signService.registerMember(requestDto);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping("/confirm-email")
    public BaseResponse<String> confirmEmail(@RequestBody EmailAuthRequestDto requestDto) throws BaseException {
        signService.confirmEmail(requestDto);
        return new BaseResponse<>("인증이 완료되었습니다.");
    }

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponseDto> login(@RequestBody MemberLoginRequestDto requestDto) throws BaseException {
        MemberLoginResponseDto responseDto = signService.loginMember(requestDto);
        return new BaseResponse<>(responseDto);
    }

    @PostMapping("/reissue")
    public BaseResponse<TokenResponseDto> reIssue(@RequestBody TokenRequestDto tokenRequestDto) throws BaseException {
        TokenResponseDto responseDto = signService.reIssue(tokenRequestDto);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping(value = "/login/{provider}")
    public void socialLoginType(@PathVariable(name = "provider")
                                String provider) {
        socialService.request(provider);
    }

    @GetMapping(value = "/login/{provider}/callback")
    public BaseResponse<MemberLoginResponseDto> callback(@PathVariable(name = "provider") String provider,
                                                         @RequestParam(name = "code") String code) {
        MemberLoginResponseDto responseDto = signService.loginMemberByProvider(code, provider);
        return new BaseResponse<>(responseDto);
    }

}
