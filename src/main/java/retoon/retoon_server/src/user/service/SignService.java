package retoon.retoon_server.src.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.user.converter.ProviderConverter;
import retoon.retoon_server.src.user.entity.EmailAuth;
import retoon.retoon_server.src.user.entity.Provider;
import retoon.retoon_server.src.user.entity.User;
import retoon.retoon_server.src.user.model.*;
import retoon.retoon_server.src.user.repository.EmailAuthRepository;
import retoon.retoon_server.src.user.repository.UserRepository;
import retoon.retoon_server.utils.JwtService;
import retoon.retoon_server.utils.SHA256;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static retoon.retoon_server.utils.ValidationRegex.isRegexEmail;
import static retoon.retoon_server.utils.ValidationRegex.isRegexPassword;

@Service
@RequiredArgsConstructor
public class SignService {
    private final JwtService jwtService;
    private final SocialService socialService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailAuthRepository emailAuthRepository;
    private final ProviderConverter providerConverter;

    /** 회원가입 */
    @SneakyThrows
    @Transactional
    public UserRegisterResDto registerUser(UserRegisterReqDto requestDto) throws BaseException {
        checkSignUp(requestDto); // 회원가입 시 예외사항 처리
        // 이메일 인증 저장
        EmailAuth emailAuth = emailAuthRepository.save(
                EmailAuth.builder()
                        .email(requestDto.getEmail())
                        .authToken(UUID.randomUUID().toString().substring(0, 6))
                        .expired(false)
                        .build());

        // 일반 사용자 저장
        User user = userRepository.save(
                User.builder()
                        .email(requestDto.getEmail())
                        .name(requestDto.getName())
                        .password(encryptPwd(requestDto.getPassword()))
                        .provider("local")
                        .emailAuth(false)
                        .createdAT(LocalDateTime.now())
                        .updatedAT(LocalDateTime.now())
                        .status("ACTIVE")
                        .build());

        // 이메일 전송
        emailService.send(emailAuth.getEmail(), emailAuth.getAuthToken());
        return UserRegisterResDto.builder()
                .userIdx(user.getUserIdx())
                .email(user.getEmail())
                .authToken(emailAuth.getAuthToken())
                .build();
    }


    /** SNS 로그인 */
    @Transactional
    public UserLoginResDto loginUserByProvider(String code, String provider){
        String accessToken = socialService.extractAccessToken(provider, code);
        SocialProfileDto profile = socialService.getUserProfileInfo(provider, accessToken);

        Optional<User> findUser = userRepository.findByEmailAndProvider(profile.getEmail(), provider);
        if (findUser.isPresent()) { // 유저가 존재하는 경우
            User user = findUser.get();
            user.updateAccessToken(jwtService.createJwt(user.getUserIdx()));
            user.updateRefreshToken(jwtService.createRefreshJwt());
            user.setUpdatedAT();
            return new UserLoginResDto(user.getUserIdx(), user.getAccessToken(), user.getRefreshToken());
        }
        else{ // 새롭게 회원가입이 필요한 경우
            User saveUser = saveUser(profile, provider);
            saveUser.updateAccessToken(jwtService.createJwt(saveUser.getUserIdx()));
            saveUser.updateRefreshToken(jwtService.createRefreshJwt());
            return new UserLoginResDto(saveUser.getUserIdx(), saveUser.getAccessToken(), saveUser.getRefreshToken());
        }
    }

    /** SNS 로그인 사용자 저장 */
    private User saveUser(SocialProfileDto profile, String provider) {
        User user = User.builder()
                .name(profile.getName())
                .email(profile.getEmail())
                .password(null)
                .provider(provider)
                .createdAT(LocalDateTime.now())
                .updatedAT(LocalDateTime.now())
                .status("ACTIVE")
                .build();
        return userRepository.save(user);
    }

    /** 이메일 인증 확인 */
    public void confirmEmail(EmailAuthReqDto requestDto) throws BaseException {
        Optional<EmailAuth> emailAuth = emailAuthRepository.findValidAuthByEmail(requestDto.getEmail(), requestDto.getAuthToken(), LocalDateTime.now());
        if(emailAuth.isEmpty()){ throw new BaseException(BaseResponseStatus.NOT_VALID_AUTH_TOKEN); }

        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if(user.isEmpty()) {throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        emailAuth.get().useToken(); // 토큰 사용 후 만료
        user.get().emailVerifiedSuccess(); // 인증 성공
        emailAuthRepository.saveAndFlush(emailAuth.get());
        userRepository.saveAndFlush(user.get()); // DB 반영
    }

    /** 회원가입 시 예외사항 확인 */
    public void checkSignUp(UserRegisterReqDto requestDto) throws BaseException {
        // 사용자 이름을 입력하지 않은 경우
        if(requestDto.getName() == null || requestDto.getName().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_NAME);
        }

        // 사용자 이메일을 입력하지 않은 경우
        if (requestDto.getEmail() == null || requestDto.getEmail().equals("")) {
            throw new BaseException(BaseResponseStatus.EMPTY_USER_EMAIL);
        }

        // 기존에 존재하는 이메일인 경우
        if(userRepository.existsByEmail(requestDto.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }

        // 이메일 정규표현식이 아닌 경우
        if(!isRegexEmail(requestDto.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        // 사용자 비밀번호를 입력하지 않은 경우
        if(requestDto.getPassword() == null || requestDto.getPassword().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_PASSWORD);
        }

        // 비밀번호가 영문, 숫자, 특수문자를 섞어서 넣지 않은 경우
        if(!isRegexPassword(requestDto.getPassword())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
        }

        // 사용자 비밀번호 확인을 한번 더 입력하지 않은 경우
        if(requestDto.getPasswordCheck() == null || requestDto.getPasswordCheck().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_CHECK_PASSWORD);
        }

        // 사용자 비밀번호와 비밀번호 확인이 일치하지 않는 경우
        if(!requestDto.getPasswordCheck().equals(requestDto.getPassword())){
            throw new BaseException(BaseResponseStatus.NOT_EQUAL_PASSWORD);
        }
    }

    /** 로그인 시 예외사항 확인 */
    public void checkLogin(UserLoginReqDto requestDto) throws BaseException {
        // 이메일을 입력하지 않은 경우
        if(requestDto.getEmail() == null || requestDto.getEmail().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_EMAIL);
        }
        // 이메일 정규표현식이 아닌 경우
        if(!isRegexEmail(requestDto.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        // 비밀번호를 입력하지 않은 경우
        if(requestDto.getPassword() == null || requestDto.getPassword().equals("")){
            throw new BaseException(BaseResponseStatus.EMPTY_USER_PASSWORD);
        }
    }

    /** 로그인 */
    @Transactional
    public UserLoginResDto loginUser(UserLoginReqDto requestDto) throws BaseException {
        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if(user.isEmpty()){ throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN); }

        String encryptPwd = encryptPwd(requestDto.getPassword());
        if(!user.get().getPassword().equals(encryptPwd)) { throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN); }

        user.get().updateAccessToken(jwtService.createJwt(user.get().getUserIdx()));
        user.get().updateRefreshToken(jwtService.createRefreshJwt());
        return new UserLoginResDto(user.get().getUserIdx(), user.get().getAccessToken(), user.get().getRefreshToken());
    }

    /** 토큰 재발급 */
    @Transactional
    public TokenResDto reIssue(TokenReqDto requestDto) throws BaseException {
        Optional<User> user = userRepository.findByUserIdx(jwtService.getUserIdx());

        if(!user.get().getRefreshToken().equals(requestDto.getRefreshToken())) // 리프레시 토큰이 일치하지 않는 경우
            throw new BaseException(BaseResponseStatus.NOT_VALID_REFRESH_TOKEN);

        String accessToken = jwtService.createJwt(user.get().getUserIdx());
        String refreshToken = jwtService.createRefreshJwt();
        user.get().updateAccessToken(accessToken);
        user.get().updateRefreshToken(refreshToken);
        return new TokenResDto(accessToken, refreshToken);
    }

    /** 토큰 만료 확인 */
    public void checkToken(TokenReqDto requestDto) throws BaseException {
        if(!jwtService.verifyJwt(requestDto.getRefreshToken())) // 토큰이 만료된 경우
            throw new BaseException(BaseResponseStatus.NOT_VALID_REFRESH_TOKEN);
    }

    /** 이메일 중복 확인 */
    public void validateDuplicated(String email) throws BaseException {
        if (userRepository.findByEmail(email).isPresent()){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL); } // 중복된 이메일이 존재
    }

    /** 비밀번호 암호화 */
    public String encryptPwd(String pwd) throws BaseException {
        // 비밀번호 암호화
        String encryptPwd;
        try{
            // 비밀번호 암호화
            new SHA256();
            encryptPwd = SHA256.encrypt(pwd);
        }
        catch(Exception e){
            // 비밀번호 암호화 실패
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        return encryptPwd;
    }

    /** 회원 비활성화 */
    public void disabledUser (int userIdx) throws BaseException {
        // 유저 인덱스를 통한 객체 반환
        Optional<User> user = userRepository.findByUserIdx(userIdx);
        // 유저 정보 존재 여부 확인
        if(user.isPresent()){
            User getUser = user.get(); // 유저 정보 획득
            getUser.changeUserInactive(); // 유저 비활성화
            userRepository.saveAndFlush(getUser); // DB 반영
        }
        else{
            // 유저가 존재하지 않는 경우
            throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS);
        }
    }

    /** 비밀번호 일치 확인 */
    public void checkEqualPwd(UserPasswordReqDto requestDto) throws BaseException {
        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if(user.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }
        if(providerConverter.convert(user.get().getProvider()) != Provider.LOCAL){ throw new BaseException(BaseResponseStatus.CANNOT_RESET_PASSWORD); }

        String encryptPwd = encryptPwd(requestDto.getPassword());
        if(!user.get().getPassword().equals(encryptPwd)) { throw new BaseException(BaseResponseStatus.NOT_EQUAL_PASSWORD); }
    }

    /** 비밀번호 재설정 */
    @Transactional
    public void resetPwd(UserPasswordResetReqDto requestDto) throws BaseException {
        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if(user.isEmpty()) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        if(requestDto.getPassword() == null || requestDto.getPassword().equals("")){ // 사용자 비밀번호를 입력하지 않은 경우
            throw new BaseException(BaseResponseStatus.EMPTY_USER_PASSWORD);
        }
        if(!isRegexPassword(requestDto.getPassword())){ // 비밀번호가 영문, 숫자, 특수문자를 섞어서 넣지 않은 경우
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
        }
        if(encryptPwd(requestDto.getPassword()).equals(user.get().getPassword())) {
            throw new BaseException(BaseResponseStatus.EQUAL_BEFORE_PASSWORD); // 이전에 사용한 비밀번호와 같은 경우
        }
        if(requestDto.getPasswordCheck() == null || requestDto.getPasswordCheck().equals("")){ // 사용자 비밀번호 확인을 한번 더 입력하지 않은 경우
            throw new BaseException(BaseResponseStatus.EMPTY_USER_CHECK_PASSWORD);
        }
        if(!requestDto.getPasswordCheck().equals(requestDto.getPassword())){ // 사용자 비밀번호와 비밀번호 확인이 일치하지 않는 경우
            throw new BaseException(BaseResponseStatus.NOT_EQUAL_PASSWORD);
        }

        String encryptPwd = encryptPwd(requestDto.getPassword());
        user.get().setPassword(encryptPwd);
        userRepository.saveAndFlush(user.get()); // DB 반영
    }

}
