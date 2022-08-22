package retoon.retoon_server.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retoon.retoon_server.src.review.entity.Review;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentReq {
    private Long reviewIdx;
    private String commentText;
}

