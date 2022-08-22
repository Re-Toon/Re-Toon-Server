package retoon.retoon_server.src.review.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nimbusds.jose.JWEHeader;
import lombok.*;
import retoon.retoon_server.src.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue
    private Long commentIdx;

    @NonNull
    private String commentText;

    @NonNull
    private LocalDateTime createdAT;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "review_idx")
    private Review review;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "user_idx")
    private User user;
}
