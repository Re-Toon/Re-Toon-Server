package retoon.retoon_server.src.user;


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
import retoon.retoon_server.src.user.model.*;
import retoon.retoon_server.src.user.model.mypage.GetUserFollowRes;
import retoon.retoon_server.src.user.model.mypage.GetUserProfileRes;
import retoon.retoon_server.src.user.repository.FollowRepository;
import retoon.retoon_server.src.user.repository.UserRepository;
import retoon.retoon_server.src.user.social.SocialLoginType;
import retoon.retoon_server.utils.JwtService;

import java.util.List;

import static retoon.retoon_server.config.BaseResponseStatus.*;
import static retoon.retoon_server.utils.ValidationRegex.isRegexEmail;

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
    @Autowired
    private final FollowRepository followRepository;

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
                                                    @RequestParam(name = "code") String code) {
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
     * parameter postJoinReq
     * return postLoginRes
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
     * POST / 로그인 API
     * parameter postLoginReq
     * return postLoginRes
     * */
    @PostMapping(value = "/login")
    public BaseResponse<PostLoginUserRes> loginUser(@RequestBody PostLoginUserReq postLoginUserReq){
        try{
            // 이메일을 입력하지 않은 경우
            if(postLoginUserReq.getEmail() == null || postLoginUserReq.getEmail().equals("")){
                return new BaseResponse<>(EMPTY_USER_EMAIL);
            }
            // 이메일 정규표현식이 아닌 경우
            if(!isRegexEmail(postLoginUserReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            // 비밀번호를 입력하지 않은 경우
            if(postLoginUserReq.getPassword() == null || postLoginUserReq.getPassword().equals("")){
                return new BaseResponse<>(EMPTY_USER_PASSWORD);
            }
            PostLoginUserRes postLoginUserRes = userService.loginUser(postLoginUserReq);
            return new BaseResponse<>(postLoginUserRes);
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
    @GetMapping("/login/{userIdx}")
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

    @PostMapping("/profile/{userIdx}")
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

    @PatchMapping("/profile/{userIdx}")
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

    /**
     * POST / 팔로우 API
     * parameter userIdx(팔로우 당하는 유저 인덱스)
     * return 새로 생성된 팔로우 객체
     * 팔로우 정보 저장 후  follow 객체 반환
     * */
    @PostMapping("/follow/{fromUserIdx}/{toUserIdx}")
    public BaseResponse<PostFollowRes> followUser(@PathVariable("fromUserIdx") int fromUserIdx, @PathVariable("toUserIdx") int toUserIdx) throws BaseException {
        User follower = userRepository.findByUserIdx(fromUserIdx);
        if(follower == null) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        User followee = userRepository.findByUserIdx(toUserIdx);
        if(followee == null) { throw new BaseException(BaseResponseStatus.NOT_EXIST_USERS); }

        PostFollowRes follow = userService.followUser(fromUserIdx, toUserIdx); // 팔로우 정보 반환
        return new BaseResponse<>(follow); // 팔로우 정보 전달
    }


    /**
     * DELETE / 언팔로우 API
     * parameter toUserId 언팔로우 당하는 유저의 인덱스
     * fromUserId를 가진 유저가 toUserId를 가진 유저를 팔로우하는 정보를 삭제
     * */
    @DeleteMapping("/follow/{fromUserIdx}/{toUserIdx}")
    public BaseResponse<String> unFollowUser(@PathVariable("fromUserIdx") int fromUserIdx, @PathVariable("toUserIdx") int toUserIdx) throws BaseException {
        int unFollowIdx = userService.getFollowIdxByFromToUserIdx(fromUserIdx, toUserIdx); // 언팔로우할 정보 반환
        if(unFollowIdx == -1){ return new BaseResponse<>(BaseResponseStatus.NOT_EXISTS_FOLLOW_INFO);} // 정보가 없는 경우에 팔로우 실패
        followRepository.deleteById(unFollowIdx); // 팔로우한 정보 삭제
        String result = "팔로우 비활성화 되었습니다.";
        return new BaseResponse<>(result);
    }

    /**
     * GET / 마이페이지 프로필 조회 API
     * parameter userIdx, login email 현재 마이페이지 유저의 인덱스, 로그인한 유저 이메일
     * return 마이페이지 내 프로필 부분에 닉네임, 자기소개, 이미지, 팔로잉 수, 팔로워 수 반환
     * */
    @GetMapping("profile/{userIdx}")
    public BaseResponse<GetUserProfileRes> myPage(@PathVariable("userIdx") int userIdx, @RequestParam String loginEmail){
        GetUserProfileRes getUserProfileRes = userService.getProfile(userIdx, loginEmail);
        return new BaseResponse<>(getUserProfileRes);
    }

    /**
     * GET / 마이페이지 리뷰어 팔로워 목록 조회
     * parameter userIdx, login email 현재 마이페이지 유저의 인덱스, 로그인한 유저 이메일
     * */
    @GetMapping("/follow/{userIdx}/follower")
    public BaseResponse<List<GetUserFollowRes>> getFollower(@PathVariable("userIdx") int userIdx, @RequestParam String loginEmail){
        return new BaseResponse<>(userService.getFollowerListByUserIdx(userIdx, loginEmail));
    }

    /**
     * GET / 마이페이지 리뷰어 팔로잉 목록 조회
     * parameter userIdx, login email 현재 마이페이지 유저의 인덱스, 로그인한 유저 이메일
     * */
    @GetMapping("/follow/{userIdx}/following")
    public BaseResponse<List<GetUserFollowRes>> getFollowing(@PathVariable("userIdx") int userIdx, @RequestParam String loginEmail){
        return new BaseResponse<>(userService.getFollowingListByUserIdx(userIdx, loginEmail));
    }

}