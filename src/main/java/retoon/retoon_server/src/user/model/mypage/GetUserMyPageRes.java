package retoon.retoon_server.src.user.model.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 마이페이지 조회 시 사용 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserMyPageRes {
    private GetUserProfileRes getUserProfileRes; // 유저 정보를 담은 객체
    // private List<GetUserReviewRes> getUserReviewList; // 유저 리뷰 목록을 담은 객체
    // private List<GetUserCommendRes> getUserCommendList; // 유저 추천 목록을 담은 객체
    // private List<GetUserReadingListRes> getUserReadingList; // 유저 리딩리스트를 담은 객체
}
