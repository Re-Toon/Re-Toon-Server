package retoon.retoon_server.src.user.information;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//클라이언트로 보낼 사용자 정보 객체
@Getter
@Setter
@AllArgsConstructor
public class PostSocialUserRes {
    //클라이언트로 보낼 jwt token, access token 담긴 객체
    //private String accessToken; // 구글 API 연결을 위한 access token
    //private String tokenType; // token type
    private int userIdx; //db 내 유저 인덱스
    private String name; //사용자 이름
    private String email; //사용자 이메일
    private String jwtToken; // 서버 자체에서 생성한 jwt token
}
