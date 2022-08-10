package retoon.retoon_server.src.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.user.model.PatchUserReq;
import retoon.retoon_server.src.user.model.PostUserReq;

@RestController //controller + ResponseBody
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;

    /**
     * POST / 프로필 생성 API
     * parameter userIdx, userProfile 생성하고자 하는 유저의 정보를 담은 객체
     * return String
     * */

    @PostMapping("")
    public BaseResponse<String> createProfile(@RequestBody PostUserReq postUserReq){
        try{
            //UserProfile makeProfile = userService.createProfile(postUserReq); // 유저 프로필 생성
            userService.createProfile(postUserReq); // 유저 프로필 생성
            log.info("프로필 생성 성공");
            String result = "프로필 정보 생성을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            log.info("프로필 생성 실패");
            return new BaseResponse<>((e.getStatus()));
        }
    }


    /**
     * PATCH / 프로필 수정 API
     * parameter userIdx, patchUserReq 수정 가능한 유저의 정보를 담은 객체
     * return String
     * */

    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyProfile(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq){
        try{
            //UserProfile newProfile = userService.modifyProfile(userIdx, patchUserReq);
            userService.modifyProfile(userIdx, patchUserReq); // 유저 프로필 수정
            log.info("프로필 수정 성공");
            String result = "프로필 정보 수정을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            log.info("프로필 수정 실패");
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * PATCH / 회원 탈퇴 API
     * parameter userIdx
     * return String
     * */
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<String> deleteProfile(@PathVariable("userIdx") int userIdx){
        try{
            log.info("회원 탈퇴 성공");
            userService.deleteProfile(userIdx); // 회원 탈퇴 진행
            String result = "회원 탈퇴를 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch(BaseException e){
            log.info("회원 탈퇴 실패");
            return new BaseResponse<>(e.getStatus());
        }
    }
}