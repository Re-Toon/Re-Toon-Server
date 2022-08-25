package retoon.retoon_server.src.review.entity;

import lombok.*;
import retoon.retoon_server.src.user.entity.User;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "review_unlike")
public class ReviewUnlike {
    @Id
    @GeneratedValue
    private Long reviewUnlikeIdx;

    @ManyToOne
    @JoinColumn(name = "review_idx")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;
}