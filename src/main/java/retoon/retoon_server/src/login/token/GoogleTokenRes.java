package retoon.retoon_server.src.login.token;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GoogleTokenRes {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String id_token;
    private String scope;
}


