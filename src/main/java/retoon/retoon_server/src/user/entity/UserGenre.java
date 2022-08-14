package retoon.retoon_server.src.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor // 빈 기본 생성자 생략 가능
@Getter
@Setter
@Entity
public class UserGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성 규칙
    private int genreIdx; // 유저 선택 장르 인덱스

    @Column(length = 45, nullable = false)
    private String genreName; // 유저 선택 장르

    @ManyToOne
    @JoinColumn(name = "userIdx")
    @JsonBackReference // 순환참조 문제를 해결하기 위해 적용, 직렬화가 되지 않도록 수행
    private User user;

}


