package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostJoinUserRes {
    // 유저 인덱스
    int userIdx;
    // 유저 설정 이름
    String name;
    // 유저 이메일
    String email;
}
