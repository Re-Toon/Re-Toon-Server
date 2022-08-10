package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/* 프로필 수정에 활용할 객체 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserReq {
    // 프로필 수정, 어떤 유저가 수정을 하는지, 어떤 항목을 수정할지를 결정
    private String nickname;
    private String introduce;
    private String imgUrl;
    private List<PostGenreReq> genres;
}
