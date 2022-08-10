package retoon.retoon_server.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 마이페이지 내 유저 정보 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserInfoRes {
    private String nickname; // 닉네임
    private String introduce; // 자기소개
    private String profileImgUrl; // 프로필 이미지
    private int followingCount; // 팔로잉한 리뷰어의 수
    private int followerCount; // 팔로우한 리뷰어의 수
    private int reviewCount; // 작성한 리뷰의 수
}
