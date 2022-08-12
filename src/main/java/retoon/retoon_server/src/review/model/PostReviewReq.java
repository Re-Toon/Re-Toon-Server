package retoon.retoon_server.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewReq {
    private int webtoonIdx;
    private String reviewText;
    private int reviewStarRate;
    private boolean spoiler;
}

