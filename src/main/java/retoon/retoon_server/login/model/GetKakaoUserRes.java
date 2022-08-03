package retoon.retoon_server.login.model;

import lombok.*;

//실제로 사용하지는 않았으나 사용자 정보 응답 시에 반환되는 값을 저장
@Getter
@ToString
@NoArgsConstructor
// kakao 로그인 시 획득한 유저 정보를 받을 객체
public class GetKakaoUserRes {
    long id; //회원 번호
    String connected_at; //서비스 연결 완료 시각
    Properties properties;
    KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class Properties {
        //프로필과 유사한 정보, 사용자 프로퍼티
        String nickname;
        String profile_image;
        String thumbnail_image;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        //프로필 닉네임 정보
        boolean profile_nickname_needs_agreement;
        boolean profile_image_needs_agreement;
        Profile profile;
        //이메일 정보
        boolean has_email;
        boolean email_needs_agreement;
        boolean is_email_valid;
        boolean is_email_verified;
        String email;
        //나이 정보
        boolean has_age_range;
        boolean age_range_needs_agreement;
        //생일 정보
        boolean has_birthday;
        boolean birthyear_needs_agreement;
        String birthday;
        String birthday_type;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            //프로필 정보
            String nickname;
            String thumbnail_image_url;
            String profile_image_url;
            boolean is_default_image;
        }
    }

}
