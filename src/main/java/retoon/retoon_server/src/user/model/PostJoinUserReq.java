package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retoon.retoon_server.src.user.entity.User;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostJoinUserReq {
    String name; // 이름
    String email; // 이메일
    String password; // 비밀번호
    String passwordCheck; // 비밀번호 확인

    // 사용자 객체로 반환, 회원가입 시에 password 우선적으로 삽입
    public User toUser(String password){
        return new User(email, name, password);
    }
}
