package retoon.retoon_server.login.token;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NaverTokenRes {
    private String token_type;
    private String access_token;
    private String refresh_token;
    private Integer expires_in;
}
