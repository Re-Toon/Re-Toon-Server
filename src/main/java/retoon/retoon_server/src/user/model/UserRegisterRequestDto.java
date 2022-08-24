package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDto {
    private String name; // 이름
    private String email; // 이메일
    private String password; // 비밀번호
    private String passwordCheck; // 비밀번호 확인
}
