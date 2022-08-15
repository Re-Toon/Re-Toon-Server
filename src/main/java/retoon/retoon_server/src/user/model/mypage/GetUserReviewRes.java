package retoon.retoon_server.src.user.model.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 작성 리뷰 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserReviewRes {
    private int reviewIdx; // 작성 리뷰 인덱스
    private String name; // 작품 이름
    private String writerName; // 작가 이름
    private String reviewText; // 리뷰 작성 내용
    private float appStarRate; // 작품 내 평점
    private int reviewLikeCount; // 리뷰 좋아요 수
    private int reviewHateCount; // 리뷰 싫어요 수
    private int commentCount; // 리뷰 댓글 수
}
