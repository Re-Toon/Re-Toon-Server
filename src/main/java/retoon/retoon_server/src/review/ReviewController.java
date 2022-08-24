package retoon.retoon_server.src.review;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.src.review.model.PostCommentReq;
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
     * [PATCH] /reviews?reviewIdx=
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
     * [DELETE] /reviews?reviewIdx=
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

    /**
     * 리뷰 좋아요
     * [POST] /reviews/like?reviewIdx=
     */
    @PostMapping("/like")
    public BaseResponse addReviewLike(@RequestParam Long reviewIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.addReviewLike(userIdx, reviewIdx);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 좋아요 취소
     * [DELETE] /reviews/like?reviewIdx=
     */
    @DeleteMapping("/like")
    public BaseResponse deleteReviewLike(@RequestParam(value = "reviewIdx", required = true) Long reviewIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.deleteReviewLike(userIdx, reviewIdx);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 싫어요
     * [POST] /reviews/unlike?reviewIdx=
     */
    @PostMapping("/unlike")
    public BaseResponse addReviewUnlike(@RequestParam Long reviewIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.addReviewUnlike(userIdx, reviewIdx);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 싫어요 취소
     * [DELETE] /reviews/like?reviewIdx=
     */
    @DeleteMapping("/unlike")
    public BaseResponse deleteReviewUnlike(@RequestParam(value = "reviewIdx", required = true) Long reviewIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.deleteReviewUnlike(userIdx, reviewIdx);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 게시물 조회
     * [GET] /reviews?reviewIdx=
     */
    @GetMapping("")
    public BaseResponse getReview(@RequestParam Long reviewIdx) {
        try {
            return new BaseResponse(reviewService.getReview(reviewIdx));
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 댓글 추가
     * [POST] /reviews/comments
     */
    @PostMapping("/comments")
    public BaseResponse createComment(@RequestBody PostCommentReq postCommentReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.createComment(userIdx, postCommentReq);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 댓글 삭제
     * [PATCH] /reviews/comments?commentIdx=
     */
    @PatchMapping("/comments")
    public BaseResponse deleteComment(@RequestParam Long commentIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            reviewService.deleteComment(userIdx, commentIdx);
            return new BaseResponse("OK");
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 리뷰 리스트 조회
     * [GET] /reviews/lists?sort=
     */
    @GetMapping("/lists")
    public BaseResponse getReviewList(@RequestParam int sort) {
        try {
            return new BaseResponse(reviewService.getReviewList(sort));
        } catch (BaseException exception) {
            return new BaseResponse(exception.getStatus());
        }
    }


}
