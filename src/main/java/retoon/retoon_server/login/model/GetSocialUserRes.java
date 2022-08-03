package retoon.retoon_server.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import retoon.retoon_server.login.repository.OauthUser;

//구글, 네이버, 카카오에서 공통적으로 얻어올 수 있는 사용자 정보 객체
@Getter
@Setter
@AllArgsConstructor
public class GetSocialUserRes {
    private String name; //사용자 이름
    private String email; //사용자 이메일

    public OauthUser toUser(String accessToken){
        return new OauthUser(email, name, accessToken);
    }
}
