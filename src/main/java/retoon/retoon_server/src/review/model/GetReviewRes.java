package retoon.retoon_server.src.review.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retoon.retoon_server.src.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetReviewRes {
    private String nickname;
    private String reviewText;
    private int reviewStarRate;
    private boolean spoiler;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAT;

    private int reviewLike;
    private int reviewUnlike;
    private List<GetCommentRes> comments;

    /*Entity ->Dto*/
    public GetReviewRes(Review review) {
        this.nickname = review.getUser().getNickname();
        this.reviewText = review.getReviewText();
        this.reviewStarRate = review.getReviewStarRate();
        this.updatedAT = review.getUpdatedAT();
        this.reviewLike = review.getReviewLikes().size();
        this.reviewUnlike = review.getReviewUnlikes().size();
    }


}

