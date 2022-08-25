package retoon.retoon_server.src.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int followIdx;

    @JoinColumn(name = "fromUserIdx")
    @ManyToOne
    private User fromUser; // 팔로우 하는 사용자

    @JoinColumn(name = "toUserIdx")
    @ManyToOne
    private User toUser; // 팔로우 당하는 사용자

    @Builder
    public Follow(User fromUser, User toUser){
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}
