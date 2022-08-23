package retoon.retoon_server.src.login.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;

    @Column(columnDefinition = "TEXT")
    private String refreshToken; // 리프레시 토큰

    // 지연 로딩으로 생성된 프록시를 초기화, 연관된 데이터를 가지고 오는 것이 불가능한 상황을 초래 = 영속성 문제
    @ElementCollection(fetch = FetchType.EAGER) // 영속성 문제 발생으로 인해 fetch = FetchType.LAZY 에서 변경
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>();

    @Builder
    public Member(String email, String password, List<Role> roles){
        this.email = email;
        this.password = password;
        this.roles = Collections.singletonList(Role.ROLE_MEMBER);
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
