package retoon.retoon_server.src.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.review.entity.Review;
import retoon.retoon_server.src.review.model.PostReviewReq;
import retoon.retoon_server.src.review.repository.ReviewRepository;
import retoon.retoon_server.src.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    @Autowired
    private final UserRepository userRepository;

    public void checkReview(int userIdx, PostReviewReq postReviewReq) throws BaseException {
        if (userRepository.existsById(userIdx) == false)
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        if (postReviewReq.getReviewText() == null || postReviewReq.getReviewText() == "")
            throw new BaseException(BaseResponseStatus.EMPTY_REVIEW_TEXT);
        if (postReviewReq.getReviewStarRate() < 0 || postReviewReq.getReviewStarRate() > 5)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_STAR_RATE);
    }

    @Transactional
    public void createReview(int userIdx, PostReviewReq postReviewReq) throws BaseException {
        checkReview(userIdx,postReviewReq);
        Review review = Review.builder()
                .user(userRepository.getReferenceById(userIdx))
                .webtoonIdx(postReviewReq.getWebtoonIdx())
                .reviewText(postReviewReq.getReviewText())
                .reviewStarRate(postReviewReq.getReviewStarRate())
                .isSpoiler(postReviewReq.isSpoiler())
                .createdAT(LocalDateTime.now())
                .updatedAT(LocalDateTime.now())
                .build(); // 새로운 리뷰 객체 생성

        reviewRepository.save(review);
    }

    @Transactional
    public void editReview(int userIdx, Long reviewIdx, PostReviewReq postReviewReq) throws BaseException {
        checkReview(userIdx,postReviewReq);
        if (reviewRepository.existsByReviewIdx(reviewIdx) == false)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_IDX);
        Review review = reviewRepository.getReferenceById(reviewIdx);
        if (review.getUser().getUserIdx() != userIdx)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_USER);
        review.setReviewText(postReviewReq.getReviewText());
        review.setReviewStarRate(postReviewReq.getReviewStarRate());
        review.setSpoiler(postReviewReq.isSpoiler());
    }

    @Transactional
    public void deleteReview(int userIdx, Long reviewIdx) throws BaseException {
        if (userRepository.existsById(userIdx) == false)
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        if (reviewRepository.existsByReviewIdx(reviewIdx) == false)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_IDX);
        Review review = reviewRepository.getReferenceById(reviewIdx);
        if (review.getUser().getUserIdx() != userIdx)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_USER);
        reviewRepository.deleteById(reviewIdx);
    }
}
