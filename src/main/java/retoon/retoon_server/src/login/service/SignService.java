package retoon.retoon_server.src.login.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.BasicPathUsageException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.login.entity.EmailAuth;
import retoon.retoon_server.src.login.entity.Member;
import retoon.retoon_server.src.login.model.*;
import retoon.retoon_server.src.login.repository.EmailAuthRepository;
import retoon.retoon_server.src.login.repository.MemberRepository;
import retoon.retoon_server.src.login.jwt.JwtTokenProvider;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocialService socialService;
    private final EmailService emailService;

    /** 회원가입 */
    @Transactional
    public MemberRegisterResponseDto registerMember(MemberRegisterRequestDto requestDto) throws Exception {
        validateDuplicated(requestDto.getEmail());
        // 이메일 인증 저장
        EmailAuth emailAuth = emailAuthRepository.save(
                EmailAuth.builder()
                        .email(requestDto.getEmail())
                        .authToken(UUID.randomUUID().toString().substring(0, 6))
                        .expired(false)
                        .build());
        // 일반 사용자 저장
        Member member = memberRepository.save(
                Member.builder()
                        .email(requestDto.getEmail())
                        .name(requestDto.getName())
                        .password(passwordEncoder.encode(requestDto.getPassword()))
                        .provider(null)
                        .emailAuth(false)
                        .build());
        // 이메일 전송
        emailService.send(emailAuth.getEmail(), emailAuth.getAuthToken());
        return MemberRegisterResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .authToken(emailAuth.getAuthToken())
                .build();
    }

    /** 이메일 중복 확인 */
    public void validateDuplicated(String email) throws BaseException {
        if (memberRepository.findByEmail(email).isPresent()){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL); } // 중복된 이메일이 존재
    }

    /** 이메일 인증 확인 */
    public void confirmEmail(EmailAuthRequestDto requestDto) throws BaseException {
        EmailAuth emailAuth = emailAuthRepository.findValidAuthByEmail(requestDto.getEmail(), requestDto.getAuthToken(), LocalDateTime.now())
                .orElseThrow( () -> new BaseException(BaseResponseStatus.NOT_VALID_AUTH_TOKEN));
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_EXIST_USERS));
        emailAuth.useToken(); // 토큰 사용 후 만료
        member.emailVerifiedSuccess(); // 인증 성공
        emailAuthRepository.saveAndFlush(emailAuth);
        memberRepository.saveAndFlush(member); // DB 반영
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

    /** 토큰 재발급 */
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

    /** 토큰으로 사용자 반환 */
    public Member findMemberByToken(TokenRequestDto requestDto) throws BaseException {
        Authentication auth = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String username = userDetails.getUsername();
        return memberRepository.findByEmail(username).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_EXIST_USERS));
    }

    /** SNS 로그인 */
    @Transactional
    public MemberLoginResponseDto loginMemberByProvider(String code, String provider){
        String accessToken = socialService.extractAccessToken(provider, code);
        SocialProfileDto profile = socialService.getUserProfileInfo(provider, accessToken);

        Optional<Member> findMember = memberRepository.findByEmailAndProvider(profile.getEmail(), provider);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            member.updateRefreshToken(jwtTokenProvider.createRefreshToken());
            return new MemberLoginResponseDto(member.getId(), jwtTokenProvider.createToken(findMember.get().getEmail()), member.getRefreshToken());
        }
        else{
            Member saveMember = saveMember(profile, provider);
            saveMember.updateRefreshToken(jwtTokenProvider.createRefreshToken());
            return new MemberLoginResponseDto(saveMember.getId(), jwtTokenProvider.createToken(saveMember.getEmail()), saveMember.getRefreshToken());
        }
    }

    /** SNS 로그인 사용자 저장 */
    private Member saveMember(SocialProfileDto profile, String provider) {
        Member member = Member.builder()
                .name(profile.getName())
                .email(profile.getEmail())
                .password(null)
                .provider(provider)
                .build();
        return memberRepository.save(member);
    }

}
