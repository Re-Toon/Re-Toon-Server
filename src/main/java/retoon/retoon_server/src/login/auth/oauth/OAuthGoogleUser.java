package retoon.retoon_server.src.login.auth.oauth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OAuthGoogleUser {
    private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String email;
    private String emailVerified;
    private String atHash;
    private String name;
    private String picture;
    private String givenName;
    private String familyName;
    private String locale;
    private String iat;
    private String exp;
    private String alg;
    private String kid;
    private String typ;
}
