package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponseDto {
    int followIdx; // 팔로우 정보
    FollowUserResponseDto follower; // 팔로우한 사람
    FollowUserResponseDto followee; // 팔로우 당한 사람
}
