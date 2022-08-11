package retoon.retoon_server.src.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.src.review.entity.Review;
import retoon.retoon_server.src.review.model.PostReviewReq;
import retoon.retoon_server.src.review.repository.ReviewRepository;
import retoon.retoon_server.src.user.repository.UserProfileRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void createReview(int userIdx, PostReviewReq postReviewReq) throws IOException, BaseException {
        Review review = Review.builder()
                .user(userProfileRepository.getReferenceById(userIdx))
                .webtoonIdx(postReviewReq.getWebtoonIdx())
                .reviewText(postReviewReq.getReviewText())
                .reviewStarRate(postReviewReq.getReviewStarRate())
                .isSpoiler(postReviewReq.isSpoiler())
                .createdAT(LocalDateTime.now())
                .updatedAT(LocalDateTime.now())
                .status("ACTIVE")
                .build(); // 새로운 리뷰 객체 생성

        reviewRepository.save(review);
    }


}
