package retoon.retoon_server.src.user.model.mypage;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// 리딩 리스트 자체 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserReadingListRes {
    private int readingListIdx; // 리딩 리스트 인덱스
    private String name; // 리딩 리스트 자체의 이름
    private String imgUrl; // 리딩 리스트 자체의 이미지
    private int readingListObjCount; // 리딩 리스트 내 작품의 개수
    private List<GetUserReadingListObjRes> readingListObj; // 리딩 리스트 내 작품 객체
}
