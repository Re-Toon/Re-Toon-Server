package retoon.retoon_server.src.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.login.entity.Member;
import retoon.retoon_server.src.login.model.*;
import retoon.retoon_server.src.login.repository.MemberRepository;
import retoon.retoon_server.src.login.token.JwtTokenProvider;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SignService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /** 회원가입 */
    @Transactional
    public MemberRegisterResponseDto registerMember(MemberRegisterRequestDto requestDto) throws BaseException {
        validateDuplicated(requestDto.getEmail());
        Member user = memberRepository.save(
                Member.builder()
                        .email(requestDto.getEmail())
                        .password(passwordEncoder.encode(requestDto.getPassword()))
                        .build());
        return new MemberRegisterResponseDto(user.getId(), user.getEmail());
    }

    /** 이메일 중복 확인 */
    public void validateDuplicated(String email) throws BaseException {
        if (memberRepository.findByEmail(email).isPresent()){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL); } // 중복된 이메일이 존재
    }

    /** 로그인 */
    @Transactional
    public MemberLoginResponseDto loginMember(MemberLoginRequestDto requestDto) throws BaseException {
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.FAILED_TO_LOGIN));
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword()))
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());
        return new MemberLoginResponseDto(member.getId(), jwtTokenProvider.createToken(requestDto.getEmail()), member.getRefreshToken());
    }

    @Transactional
    public TokenResponseDto reIssue(TokenRequestDto requestDto) throws BaseException {
        if(!jwtTokenProvider.validateTokenExpiration(requestDto.getRefreshToken()))
            throw new BaseException(BaseResponseStatus.NOT_VALID_REFRESH_TOKEN);

        Member member = findMemberByToken(requestDto);

        if(!member.getRefreshToken().equals(requestDto.getRefreshToken()))
            throw new BaseException(BaseResponseStatus.NOT_VALID_REFRESH_TOKEN);

        String accessToken = jwtTokenProvider.createToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        member.updateRefreshToken(refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public Member findMemberByToken(TokenRequestDto requestDto) throws BaseException {
        Authentication auth = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String username = userDetails.getUsername();
        return memberRepository.findByEmail(username).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_EXIST_USERS));
    }


}
