package retoon.retoon_server.src.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
// 유저 Table 통합
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성 규칙
    private int userIdx; // 유저 인덱스

    @Column(length = 45, nullable = false)
    private String name; // 유저 이름

    @Column(length = 45, nullable = false)
    private String email; // 유저 이메일

    @Column(columnDefinition = "TEXT")
    private String password; // 유저 비밀번호

    @Column(columnDefinition = "TEXT")
    private String jwtToken; // 유저 토큰

    @Column(length = 45)
    private String nickname; // 유저 닉네임

    @Column(columnDefinition = "TEXT")
    private String introduce; // 유저 자기소개

    @Column(columnDefinition = "TEXT")
    private String imgUrl; // 유저 프로필 이미지

    @Column(columnDefinition = "varchar(45) default 'ACTIVE'", nullable = false) // 유저 삭제를 위한 상태 column 추가
    private String status = "ACTIVE"; // 유저 회원 상태

    // 유저 프로필마다 관심있는 장르 리스트가 존재
    @OneToMany(mappedBy = "user",  targetEntity = UserGenre.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) // 연관된 객체 정보도 함께 update, 변한 것만 추가
    @JsonManagedReference // 순환참조 문제를 해결하기 위해 수행, 직렬화를 수행
    private List<UserGenre> genres = new ArrayList<>();

    // 회원가입 시에 활용할 객체
    @Builder
    public User(String email, String name, String jwtToken) {
        this.email = email;
        this.name = name;
        this.jwtToken = jwtToken;
    }

    // 장르 추가 메소드
    public void addGenre(UserGenre genre) {
        this.genres.add(genre);
        genre.setUser(this); // 사용자 정보 설정
    }
}
