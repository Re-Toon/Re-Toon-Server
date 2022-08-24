package retoon.retoon_server.src.user.auth.token;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GoogleToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String id_token;
    private String scope;
}
