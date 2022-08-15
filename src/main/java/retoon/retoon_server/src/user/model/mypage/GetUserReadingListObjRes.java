package retoon.retoon_server.src.user.model.mypage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// 리딩 리스트 내 작품 정보 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserReadingListObjRes {
    private int ReadingListObjIdx; // 리딩 리스트 내 작품 객체 인덱스
    private String title; // 작품 제목
    private String imgUrl; // 작품 이미지 Url
    private String platformName; // 작품 플랫폼 이름
    private String writerName; // 작품 작가 이름
    private float starRate; // 작품 별점
    private int commendCount; // 추천 개수
    private List<GetGenreRes> genres; // 해당 장품의 장르 종류

}
