package retoon.retoon_server.src.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.user.model.FollowResDto;
import retoon.retoon_server.src.user.model.FollowListObjResDto;
import retoon.retoon_server.src.user.model.UserProfileDto;
import retoon.retoon_server.src.user.repository.FollowRepository;
import retoon.retoon_server.src.user.repository.UserRepository;
import retoon.retoon_server.src.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    /**
     * POST / 프로필 생성 API
     * parameter userIdx, UserProfileDto
     * return String
     * */

    @PostMapping("/profile/{userIdx}")
    public BaseResponse<String> createProfile(@PathVariable("userIdx") int userIdx, @RequestBody UserProfileDto profileDto){
        try{
            userService.createProfile(userIdx, profileDto); // 유저 프로필 생성
            String result = "프로필 정보 생성을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>((e.getStatus()));
        }
    }


    /**
     * PATCH / 프로필 수정 API
     * parameter userIdx, profileDto
     * return String
     * */

    @PatchMapping("/profile/{userIdx}")
    public BaseResponse<String> modifyProfile(@PathVariable("userIdx") int userIdx, @RequestBody UserProfileDto profileDto){
        try{
            userService.modifyProfile(userIdx, profileDto); // 유저 프로필 수정
            String result = "프로필 정보 수정을 완료했습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException e){
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * POST / 팔로우 API
     * parameter fromUserIdx, toUserIdx
     * return FollowResponseDto
     * */
    @PostMapping("/follow/{fromUserIdx}/{toUserIdx}")
    public BaseResponse<FollowResDto> followUser(@PathVariable("fromUserIdx") int fromUserIdx, @PathVariable("toUserIdx") int toUserIdx) throws BaseException {
        try{
            FollowResDto follow = userService.followUser(fromUserIdx, toUserIdx); // 팔로우 정보 반환
            return new BaseResponse<>(follow); // 팔로우 정보 전달
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * DELETE / 언팔로우 API
     * parameter fromUserIdx, toUserIdx
     * return String
     * */
    @DeleteMapping("/follow/{fromUserIdx}/{toUserIdx}")
    public BaseResponse<String> unFollowUser(@PathVariable("fromUserIdx") int fromUserIdx, @PathVariable("toUserIdx") int toUserIdx) throws BaseException {
        try {
            int unFollowIdx = userService.getFollowIdxByFromToUserIdx(fromUserIdx, toUserIdx); // 언팔로우할 정보 반환
            if(unFollowIdx == -1){ return new BaseResponse<>(BaseResponseStatus.NOT_EXISTS_FOLLOW_INFO);} // 정보가 없는 경우에 팔로우 실패
            followRepository.deleteById(unFollowIdx); // 팔로우한 정보 삭제
            String result = "팔로우 비활성화 되었습니다.";
            return new BaseResponse<>(result);
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * GET / 마이페이지 리뷰어 팔로워 목록 조회
     * parameter userIdx, login email
     * return
     * */
    @GetMapping("/follow/{userIdx}/follower")
    public BaseResponse<List<FollowListObjResDto>> getFollower(@PathVariable("userIdx") int userIdx, @RequestParam String loginEmail){
        try{
            return new BaseResponse<>(userService.getFollowerListByUserIdx(userIdx, loginEmail));
        }
        catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * GET / 마이페이지 리뷰어 팔로잉 목록 조회
     * parameter userIdx, login email 현재 마이페이지 유저의 인덱스, 로그인한 유저 이메일
     * */
    @GetMapping("/follow/{userIdx}/following")
    public BaseResponse<List<FollowListObjResDto>> getFollowing(@PathVariable("userIdx") int userIdx, @RequestParam String loginEmail){
        try{
            return new BaseResponse<>(userService.getFollowingListByUserIdx(userIdx, loginEmail));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
