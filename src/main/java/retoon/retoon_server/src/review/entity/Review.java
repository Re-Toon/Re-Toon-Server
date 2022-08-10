package retoon.retoon_server.src.review.entity;

import lombok.*;
import org.apache.catalina.User;
import retoon.retoon_server.src.user.UserDao;
import retoon.retoon_server.src.user.repository.UserProfile;

import javax.persistence.*;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserProfile user;

    @Column(length = 45)
    private String starRate;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAT;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAT;

    @Column(length = 45, nullable = false)
    private String status;
}
