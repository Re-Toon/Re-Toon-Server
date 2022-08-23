package retoon.retoon_server.src.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.login.model.*;
import retoon.retoon_server.src.login.service.SignService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {
    private final SignService signService;

    @PostMapping("/register")
    public BaseResponse<MemberRegisterResponseDto> register(@RequestBody MemberRegisterRequestDto requestDto) throws BaseException {
        MemberRegisterResponseDto responseDto = signService.registerMember(requestDto);
        return new BaseResponse<>(responseDto);
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
}
