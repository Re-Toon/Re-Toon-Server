package retoon.retoon_server.src.review.entity;

import com.mysql.cj.protocol.ColumnDefinition;
import lombok.*;
import retoon.retoon_server.src.user.repository.UserProfile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private UserProfile user;

    @NonNull
    private int webtoonIdx;

    @NonNull
    private int reviewStarRate;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @NonNull
    private LocalDateTime createdAT;

    @NonNull
    private LocalDateTime updatedAT;

    @NonNull
    @Column(columnDefinition = "boolean default false")
    private boolean isSpoiler;

    @NonNull
    @Column(columnDefinition = "varchar(45) default 'ACTIVE'") // review 삭제를 위한 column 추가
    private String status;
}