package retoon.retoon_server.src.user.model.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 리딩 리스트 내 작품 장르 정보 객체
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetGenreRes {
    private String genreName; // 장르 이름
}
