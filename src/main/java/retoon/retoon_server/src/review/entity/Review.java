package retoon.retoon_server.src.review.entity;

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

    private int webtoonIdx;

    @Column
    private int reviewStarRate;

    @Column(columnDefinition = "TEXT")
    private String reviewText;


    private LocalDateTime createdAT;

    private LocalDateTime updatedAT;

    @Column
    private boolean isSpoiler;

    @Column(columnDefinition = "varchar(45) default 'ACTIVE'", nullable = false) // review 삭제를 위한 column 추가
    private String status = "ACTIVE";
}