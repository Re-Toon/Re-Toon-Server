package retoon.retoon_server.src.login.auth.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import retoon.retoon_server.src.login.auth.token.KakaoToken;
import retoon.retoon_server.src.login.model.SocialProfileDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuthKakao implements OAuthSocial {
    @Value("${login.kakao.url}")
    private String KAKAO_LOGIN_BASE_URL;
    @Value("${login.kakao.client.id}")
    private String KAKAO_LOGIN_CLIENT_ID;
    @Value("${login.kakao.redirect.url}")
    private String KAKAO_LOGIN_REDIRECT_URL;
    @Value("${login.kakao.client.secret}")
    private String KAKAO_LOGIN_CLIENT_SECRET;
    @Value("${login.kakao.login.token.url}")
    private String KAKAO_LOGIN_LOGIN_TOKEN_URL;
    @Value("${login.kakao.user.info.url}")
    private String KAKAO_LOGIN_USER_INFO_URL;

    @Override
    public String getRedirectURL(){
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", KAKAO_LOGIN_CLIENT_ID);
        params.put("redirect_uri", KAKAO_LOGIN_REDIRECT_URL);
        params.put("response_type", "code");

        // Redirect URL 생성
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return KAKAO_LOGIN_BASE_URL + "?" + parameterString;
    }

    //REST API 호출 이후 응답을 받을 때까지 기다리는 동기 방식
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public String extractAccessToken(String code) throws JsonProcessingException {
        //JSON 파싱을 위한 기본 세팅
        ObjectMapper mapper = new ObjectMapper();

        //카카오 로그인의 경우, 헤더 설정이 필요
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        //URI 빌더 사용
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOGIN_LOGIN_TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", KAKAO_LOGIN_CLIENT_ID)
                .queryParam("redirect_uri", KAKAO_LOGIN_REDIRECT_URL)
                .queryParam("code", code)
                .queryParam("client_secret", KAKAO_LOGIN_CLIENT_SECRET);

        //POST 요청 URI와 헤더를 같이 전송
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(uriComponentsBuilder.toUriString(),
                        HttpMethod.POST,
                        request,
                        String.class);

        //응답 객체 상태 반환
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            //JSON 객체 반환, access_token, token_type, refresh_token, expires_in, id_token
            KakaoToken kakaoToken = mapper.readValue(responseEntity.getBody(), KakaoToken.class); //JSON -> Java Object, Token Request
            //응답 객체에서 real access token 추출
            return kakaoToken.getAccess_token();
        }
        return "카카오 엑세스 토큰 반환 실패";
    }

    @SuppressWarnings("unchecked") //Map<String, Object> 사용 시 발생하는 경고 제거
    @Override
    public SocialProfileDto getUserProfileInfo(String accessToken){
        //카카오 로그인의 경우, 사용자 정보를 받아올 시에 필요
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //헤더를 넣고, 객체를 구성
        HttpEntity request = new HttpEntity(headers);

        //REST API 호출 이후 응답을 받을 때까지 기다리는 동기 방식
        RestTemplate restTemplate = new RestTemplate();

        //POST 요청 URI와 헤더를 같이 전송
        ResponseEntity<JSONObject> responseEntity =
                restTemplate.exchange(KAKAO_LOGIN_USER_INFO_URL,
                        HttpMethod.POST,
                        request,
                        JSONObject.class);

        //객체 형태로 반환
        Map<String, Object> map = (Map<String, Object>) Objects.requireNonNull(responseEntity.getBody()).get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) map.get("profile");
        String name = (String)profile.get("nickname");
        String email = (String)map.get("email");

        //공통 사용자 객체로 변환
        SocialProfileDto socialProfileDto = new SocialProfileDto(name, email);

        //사용자 정보 추출
        System.out.println(">> kakao user information :: {}" + socialProfileDto);

        //사용자 정보 반환
        return socialProfileDto;
    }
}
