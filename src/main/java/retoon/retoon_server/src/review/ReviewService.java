package retoon.retoon_server.src.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.src.review.entity.Comment;
import retoon.retoon_server.src.review.entity.Review;
import retoon.retoon_server.src.review.entity.ReviewLike;
import retoon.retoon_server.src.review.entity.ReviewUnlike;
import retoon.retoon_server.src.review.model.GetCommentRes;
import retoon.retoon_server.src.review.model.GetReviewRes;
import retoon.retoon_server.src.review.model.PostCommentReq;
import retoon.retoon_server.src.review.model.PostReviewReq;
import retoon.retoon_server.src.review.repository.CommentRepository;
import retoon.retoon_server.src.review.repository.ReviewLikeRepository;
import retoon.retoon_server.src.review.repository.ReviewRepository;
import retoon.retoon_server.src.review.repository.ReviewUnlikeRepository;
import retoon.retoon_server.src.user.entity.User;
import retoon.retoon_server.src.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewUnlikeRepository reviewUnlikeRepository;

    private final CommentRepository commentRepository;
    @Autowired
    private final UserRepository userRepository;

    public void checkReviewReq(int userIdx, PostReviewReq postReviewReq) throws BaseException {
        if (!userRepository.existsById(userIdx))
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        if (postReviewReq.getReviewText() == null || postReviewReq.getReviewText() == "")
            throw new BaseException(BaseResponseStatus.EMPTY_REVIEW_TEXT);
        if (postReviewReq.getReviewStarRate() < 0 || postReviewReq.getReviewStarRate() > 5)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_STAR_RATE);
    }

    public void checkCommentReq(int userIdx, PostCommentReq postCommentReq) throws BaseException {
        if (!userRepository.existsById(userIdx))
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        if (postCommentReq.getCommentText() == null || postCommentReq.getCommentText() == "")
            throw new BaseException(BaseResponseStatus.EMPTY_COMMENT_TEXT);
        if (!reviewRepository.existsByReviewIdx(postCommentReq.getReviewIdx()))
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_IDX);
    }

    public void checkReview(int userIdx, Long reviewIdx) throws BaseException {
        if (!userRepository.existsById(userIdx))
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        if (!reviewRepository.existsByReviewIdx(reviewIdx))
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_IDX);
    }

    @Transactional
    public void createReview(int userIdx, PostReviewReq postReviewReq) throws BaseException {
        checkReviewReq(userIdx, postReviewReq);
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
        checkReviewReq(userIdx, postReviewReq);
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
        checkReview(userIdx, reviewIdx);
        Review review = reviewRepository.getReferenceById(reviewIdx);
        if (review.getUser().getUserIdx() != userIdx)
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_USER);
        reviewRepository.deleteById(reviewIdx);
    }

    @Transactional
    public void addReviewLike(int userIdx, Long reviewIdx) throws BaseException {
        checkReview(userIdx, reviewIdx);
        //싫어요가 존재하는 경우 싫어요 삭제 후 좋아요
        ReviewUnlike reviewUnlike = reviewUnlikeRepository.findByReviewIdxUserIdx(userIdx, reviewIdx).orElse(null);
        if (reviewUnlike != null) {
            deleteReviewUnlike(userIdx, reviewIdx);
        }
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdxUserIdx(userIdx, reviewIdx).orElse(null);
        if (reviewLike == null) {
            Review review = reviewRepository.getReferenceById(reviewIdx);
            User user = userRepository.getReferenceById(userIdx);
            ReviewLike newReviewLike = ReviewLike.builder()
                    .review(review)
                    .user(user)
                    .build();
            reviewLikeRepository.save(newReviewLike);
        } else {
            throw new BaseException(BaseResponseStatus.DUPLICATE_RESOURCE);
        }
    }

    @Transactional
    public void deleteReviewLike(int userIdx, Long reviewIdx) throws BaseException {
        checkReview(userIdx, reviewIdx);
        //reviewIdx와 userIdx가 일치하는 reviewLike를 찾아서 삭제
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdxUserIdx(userIdx, reviewIdx).orElse(null);
        if (reviewLike == null) throw new BaseException(BaseResponseStatus.EMPTY_REVIEW_LIKE);
        reviewLikeRepository.delete(reviewLike);
    }

    @Transactional
    public void addReviewUnlike(int userIdx, Long reviewIdx) throws BaseException {
        checkReview(userIdx, reviewIdx);
        //좋아요가 존재하는 경우 좋아요 삭제 후 싫어요
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdxUserIdx(userIdx, reviewIdx).orElse(null);
        if (reviewLike != null) {
            deleteReviewLike(userIdx, reviewIdx);
        }
        ReviewUnlike reviewUnlike = reviewUnlikeRepository.findByReviewIdxUserIdx(userIdx, reviewIdx).orElse(null);
        if (reviewUnlike == null) {
            Review review = reviewRepository.getReferenceById(reviewIdx);
            User user = userRepository.getReferenceById(userIdx);
            ReviewUnlike newReviewUnlike = ReviewUnlike.builder()
                    .review(review)
                    .user(user)
                    .build();
            reviewUnlikeRepository.save(newReviewUnlike);
        } else {
            throw new BaseException(BaseResponseStatus.DUPLICATE_RESOURCE);
        }
    }

    @Transactional
    public void deleteReviewUnlike(int userIdx, Long reviewIdx) throws BaseException {
        checkReview(userIdx, reviewIdx);
        ReviewUnlike reviewUnlike = reviewUnlikeRepository.findByReviewIdxUserIdx(userIdx, reviewIdx).orElse(null);
        if (reviewUnlike == null) throw new BaseException(BaseResponseStatus.EMPTY_REVIEW_UNLIKE);
        reviewUnlikeRepository.delete(reviewUnlike);
    }


    public GetReviewRes getReview(Long reviewIdx) throws BaseException {
        if (!reviewRepository.existsByReviewIdx(reviewIdx))
            throw new BaseException(BaseResponseStatus.INVALID_REVIEW_IDX);
        Review review = reviewRepository.getReferenceById(reviewIdx);
        List<Comment> comments = review.getComments();
        List<GetCommentRes> commentRes = new ArrayList<>();
        for(int i=0; i < comments.size(); i++){
            commentRes.add(new GetCommentRes(comments.get(i)));
        }
        GetReviewRes res = new GetReviewRes(review);
        res.setComments(commentRes);
        res.setCommentCnt(commentRes.size());
        return res;
    }



    @Transactional
    public void createComment(int userIdx, PostCommentReq postCommentReq) throws BaseException {
        checkCommentReq(userIdx, postCommentReq);
        Comment comment = Comment.builder()
                .commentText(postCommentReq.getCommentText())
                .createdAT(LocalDateTime.now())
                .user(userRepository.getReferenceById(userIdx))
                .review(reviewRepository.getReferenceById(postCommentReq.getReviewIdx()))
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(int userIdx, Long commentIdx) throws BaseException {
        if (!userRepository.existsById(userIdx))
            throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);
        if (!commentRepository.existsById(commentIdx))
            throw new BaseException(BaseResponseStatus.EMPTY_COMMENT_IDX);
        Comment comment = commentRepository.getReferenceById(commentIdx);
        if (comment.getUser().getUserIdx() != userIdx)
            throw new BaseException(BaseResponseStatus.INVALID_COMMENT_USER);
        commentRepository.delete(comment);
    }

}

