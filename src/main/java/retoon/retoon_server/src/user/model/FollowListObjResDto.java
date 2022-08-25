package retoon.retoon_server.src.user.model;

import lombok.*;

import java.math.BigInteger;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowListObjResDto {
    private int userIdx;
    private String nickname;
    private String imgUrl;
    private int followState; // 팔로우 관계인지의 여부
    private int loginUser; // 로그인한 사용자 여부

    public FollowListObjResDto(int userIdx, String nickname, String imgUrl, BigInteger followState, BigInteger loginUser){
        this.userIdx = userIdx;
        this.nickname = nickname;
        this.imgUrl = imgUrl;
        this.followState = followState.intValue();
        this.loginUser = loginUser.intValue();
    }
}
