package retoon.retoon_server.src.login.repository;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class OauthUser {
    //primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성 규칙
    private int user_idx;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(length = 45, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String token;

    @Builder
    public OauthUser(String email, String name, String token) {
        this.email = email;
        this.name = name;
        this.token = token;
    }

    public int getUserIdx() { return user_idx; }
    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public String getToken(){ return token; }

    public void setUserIdx(int userIdx) { this.user_idx = userIdx; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setToken(String token) {this.token = token;}

}
