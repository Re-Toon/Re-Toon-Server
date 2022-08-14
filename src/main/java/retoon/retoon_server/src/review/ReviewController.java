package retoon.retoon_server.src.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.review.model.PostReviewReq;
import retoon.retoon_server.src.review.repository.ReviewRepository;
import retoon.retoon_server.src.user.repository.UserProfileRepository;
import retoon.retoon_server.utils.JwtService;

@RestController // JSON 형태 결과값을 반환해줌 (@ResponseBody가 필요없음)
@RequiredArgsConstructor // final 객체를 Constructor Injection 해줌. (Autowired 역할)
@RequestMapping("/review")
@Slf4j
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserProfileRepository userProfileRepository;
    private final ReviewService reviewService;

    private final JwtService jwtService;


    /**
     * 리뷰 생성 하기
     * [POST] /review
     */
    @PostMapping("")
    public BaseResponse createReview(@RequestBody PostReviewReq postReviewReq) {
        try {
            //token을 이용해 로그인 상태임을 확인하는 부분 미완성
            //int userIdx = jwtService.getUserIdx();
            reviewService.createReview(6, postReviewReq);
            return new BaseResponse<>("OK");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 리뷰 생성 하기
     * [PATCH] /review
     */
    @PatchMapping("")
    public BaseResponse editReview(@RequestParam(value = "reviewIdx",required = true) Long reviewIdx, @RequestBody PostReviewReq postReviewReq) {
        try {
            reviewService.editReview(6, reviewIdx, postReviewReq);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

}