package retoon.retoon_server.src.login.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialProfileDto {
    private String name; // 사용자 이름
    private String email; // 사용자 이메일
}
