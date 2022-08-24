package retoon.retoon_server.src.user.auth.token;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NaverToken {
    private String token_type;
    private String access_token;
    private String refresh_token;
    private Integer expires_in;
}
