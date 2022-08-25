package retoon.retoon_server.src.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userIdx; // 유저 인덱스
    private String email; // 유저 이메일
    private String name; // 유저 이름
    private String password; // 유저 비밀번호
    private String provider; // SNS 로그인 종류
    private Boolean emailAuth; // 이메일 인증 여부
    @Column(columnDefinition = "TEXT")
    private String accessToken; // 엑세스 토큰
    @Column(columnDefinition = "TEXT")
    private String refreshToken; // 리프레시 토큰

    @Column(columnDefinition = "varchar(45) default 'ACTIVE'") // 유저 삭제를 위한 상태 column 추가
    private String status; // 유저 회원 상태

    @NonNull
    private LocalDateTime createdAT;
    @NonNull
    private LocalDateTime updatedAT;

    // 유저 프로필 부분
    @Column(length = 45)
    private String nickname; // 유저 닉네임

    @Column(columnDefinition = "TEXT")
    private String introduce; // 유저 자기소개

    @Column(columnDefinition = "TEXT")
    private String imgUrl; // 유저 프로필 이미지

    // 유저 프로필마다 관심있는 장르 리스트가 존재
    @OneToMany(mappedBy = "user",  targetEntity = UserGenre.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) // 연관된 객체 정보도 함께 update, 변한 것만 추가
    @JsonManagedReference // 순환참조 문제를 해결하기 위해 수행, 직렬화를 수행
    private List<UserGenre> genres = new ArrayList<>();

    // 장르 추가 메소드
    public void addGenre(UserGenre genre) {
        this.genres.add(genre);
        genre.setUser(this); // 사용자 정보 설정
    }

    public void updateAccessToken(String accessToken) { this.accessToken = accessToken;}

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void changeUserInactive() { this.status = "INACTIVE"; }

    public void setUpdatedAT() { this.updatedAT = LocalDateTime.now(); }

    public void emailVerifiedSuccess() {
        this.emailAuth = true;
    }
}
