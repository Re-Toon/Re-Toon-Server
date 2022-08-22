package retoon.retoon_server.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostFollowUserRes {
    int userIdx; // 유저 인덱스
    String imgUrl; // 유저 이미지 URL
    String nickname; // 유저 닉네임
    String introduce; // 유저 자기소개
}
