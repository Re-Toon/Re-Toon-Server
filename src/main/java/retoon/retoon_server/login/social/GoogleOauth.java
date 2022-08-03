package retoon.retoon_server.login.social;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import retoon.retoon_server.login.model.GetGoogleUserRes;
import retoon.retoon_server.login.model.GetSocialUserRes;
import retoon.retoon_server.login.token.GoogleTokenRes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import retoon.retoon_server.utils.JwtService;

@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {
    @Value("${login.google.url}")
    private String GOOGLE_LOGIN_BASE_URL;
    @Value("${login.google.client.id}")
    private String GOOGLE_LOGIN_CLIENT_ID;
    @Value("${login.google.redirect.url}")
    private String GOOGLE_LOGIN_REDIRECT_URl;
    @Value("${login.google.client.secret}")
    private String GOOGLE_LOGIN_CLIENT_SECRET;
    @Value("${login.google.login.token.url}")
    private String GOOGLE_LOGIN_LOGIN_TOKEN_URL;
    @Value("${login.google.auth.scope}")
    private String GOOGLE_LOGIN_SCOPE;
    @Value("${login.google.user.info.url}")
    private String GOOGLE_LOGIN_USER_INFO_URL;

    public String getScopeUrl(){
        return GOOGLE_LOGIN_SCOPE.replaceAll(",", "%20");
    }

    public JwtService jwtService;

    @Override
    public String getOauthRedirectURL(){
        Map<String, Object> params = new HashMap<>();
        params.put("scope", getScopeUrl());
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_LOGIN_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_LOGIN_REDIRECT_URl);
        params.put("access_type", "offline"); //refresh token 받기 위해 추가

        // Redirect URL 생성
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return GOOGLE_LOGIN_BASE_URL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken(String code) throws JsonProcessingException {
        //REST API 호출 이후 응답을 받을 때까지 기다리는 동기 방식
        RestTemplate restTemplate = new RestTemplate();

        //JSON 파싱을 위한 기본 세팅
        ObjectMapper mapper = new ObjectMapper();

        //key, value map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_LOGIN_CLIENT_ID);
        params.put("client_secret", GOOGLE_LOGIN_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_LOGIN_REDIRECT_URl);
        params.put("grant_type", "authorization_code");

        //POST 요청을 보내고 결과로 ResponseEntity 반환, ResponseEntity = Http 요청에 대한 응답 데이터를 포함하는 클래스
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(GOOGLE_LOGIN_LOGIN_TOKEN_URL, params, String.class);

        //응답 객체 상태 반환
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            //JSON 객체 반환, access_token, token_type, refresh_token, expires_in, id_token
            GoogleTokenRes googleTokenRes = mapper.readValue(responseEntity.getBody(), GoogleTokenRes.class); //JSON -> Java Object, Token Request
            //응답 객체에서 id token 추출
            return googleTokenRes.getId_token(); //구글 로그인에서 id token이 사용자 정보에 접근하는 access token
        }
        return "구글 엑세스 토큰 반환 실패";
    }

    // 사용자 정보 얻어오고 DB에 있는지 체크
    @Override
    public GetSocialUserRes getUserInfo(String accessToken) {
        //REST API 호출 이후 응답을 받을 때까지 기다리는 동기 방식
        RestTemplate restTemplate = new RestTemplate();

        //request url + id_token 담아서 전송
        String requestUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_LOGIN_USER_INFO_URL)
                .queryParam("id_token", accessToken).encode().toUriString();

        //결과 반환, 사용자 정보를 자바 객체로 역직렬화
        GetGoogleUserRes googleUserRes = restTemplate.getForObject(requestUrl, GetGoogleUserRes.class);
        System.out.println(">> google user information :: {}" + googleUserRes);

        //공통적으로 추출할 수 있는 정보에 대한 객체에 삽입
        GetSocialUserRes socialUserRes = new GetSocialUserRes(googleUserRes.getName(), googleUserRes.getEmail());
        return socialUserRes;
    }

    @Override
    public String logout(){ return ""; }

}
