package retoon.retoon_server.src.user.information;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

//실제로 사용하지는 않았으나 응답 객체 형식을 담음
@Getter
@ToString
@NoArgsConstructor
// naver 로그인 시 획득한 유저 정보를 받을 객체
public class GetNaverUserRes {
    String resultcode;
    String message;
    Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        String id;
        String nickname;
        String profile_image;
        String email;
        String mobile; //전화번호
        String name;
        String birthday;
        String birthyear;
    }

}
