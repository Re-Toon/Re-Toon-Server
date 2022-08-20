package retoon.retoon_server.src.user.model.mypage;

import lombok.*;

// 마이페이지 내 유저 정보 객체
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GetUserProfileRes {
    private int loginIdx; // 로그인 인덱스
    private boolean loginUser; // 로그인한 유저의 프로필 여부
    private boolean follow; // 현재 마이페이지의 사용자 팔로우 여부
    private String nickname; // 닉네임
    private String introduce; // 자기소개
    private String profileImgUrl; // 프로필 이미지
    private int followingCount; // 팔로잉한 리뷰어의 수
    private int followerCount; // 팔로우한 리뷰어의 수
    // private int reviewCount; // 작성한 리뷰의 수

}
