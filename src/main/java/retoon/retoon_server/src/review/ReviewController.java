package retoon.retoon_server.src.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.review.model.PostReviewReq;
import retoon.retoon_server.utils.JwtService;

@RestController // JSON 형태 결과값을 반환해줌 (@ResponseBody가 필요없음)
@RequiredArgsConstructor // final 객체를 Constructor Injection 해줌. (Autowired 역할)
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    private final JwtService jwtService;

    /**
     * 리뷰 생성하기
     * [POST] /reviews
     */
    @PostMapping("")
    public BaseResponse createReview(@RequestBody PostReviewReq postReviewReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.createReview(userIdx, postReviewReq);
            return new BaseResponse<>("OK");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 리뷰 수정하기
     * [PATCH] /reviews
     */
    @PatchMapping("")
    public BaseResponse editReview(@RequestParam(value = "reviewIdx", required = true) Long reviewIdx, @RequestBody PostReviewReq postReviewReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.editReview(userIdx, reviewIdx, postReviewReq);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 삭제하기
     * [DELETE] /reviews
     */
    @DeleteMapping("")
    public BaseResponse deleteReview(@RequestParam(value = "reviewIdx", required = true) Long reviewIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.deleteReview(userIdx, reviewIdx);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

}