package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retoon.retoon_server.src.user.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostJoinUserReq {
    String name; // 이름
    String email; // 이메일
    String password; // 비밀번호
    String passwordCheck; // 비밀번호 확인
}
