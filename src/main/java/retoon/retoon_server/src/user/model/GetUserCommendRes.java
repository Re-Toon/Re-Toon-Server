package retoon.retoon_server.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 마이페이지 내 추천 작품 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserCommendRes {
    private int commendIdx; // 추천 작품 인덱스
    private String imgUrl; // 이미지 URL
    private String title; // 작품 제목
    private String platformName; // 플랫폼 이름
    private String writerName; // 작가 이름
    private float starRate; // 별점
}
