package retoon.retoon_server.src.review.entity;

import lombok.*;
import retoon.retoon_server.src.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private User user;

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

    @OneToMany(mappedBy = "review", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReviewLike> reviewLikes;
    @OneToMany(mappedBy = "review", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReviewUnlike> reviewUnlikes;

    @OneToMany(mappedBy ="review", cascade = CascadeType.REMOVE)
    private List<Comment> comments;
}