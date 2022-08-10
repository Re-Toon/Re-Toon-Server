package retoon.retoon_server.login.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import retoon.retoon_server.config.BaseResponse;
import retoon.retoon_server.login.model.GetSocialUserRes;

public interface SocialOauth {
    /**
     * 각 Social Login 페이지로 Redirect 처리할 URL
     * 사용자로부터 로그인 요청을 받아 Social Login Server 인증용 code 요청
     */
    String getOauthRedirectURL();

    /**
     * API Server에서 받은 code를 활용하여 사용자 인증 정보 요청
     * parameter code API Server 에서 받아온 code
     * return API 서버에서 응답받은 Json 형태의 결과를 string으로 반환
     * */

    String requestAccessToken(String code) throws JsonProcessingException;

    /**
     * API Server에서 받은 id token or access token을 활용해서 사용자 인증 정보 반환
     * */
    GetSocialUserRes getUserInfo(String accessToken);

    /**
     * logout api
     * */
    String logout();
}