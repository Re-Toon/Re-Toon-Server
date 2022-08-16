package retoon.retoon_server.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetReviewRes {
    private int userIdx;
    private String reviewText;
    private int reviewStarRate;
    private boolean spoiler;
    private LocalDateTime updatedAT;
}
