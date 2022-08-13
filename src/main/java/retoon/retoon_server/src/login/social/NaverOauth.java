package retoon.retoon_server.src.login.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import retoon.retoon_server.src.login.model.GetSocialUserRes;
import retoon.retoon_server.src.login.token.NaverTokenRes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NaverOauth implements SocialOauth {
    @Value("${login.naver.url}")
    private String NAVER_LOGIN_BASE_URL;
    @Value("${login.naver.client.id}")
    private String NAVER_LOGIN_CLIENT_ID;
    @Value("${login.naver.redirect.url}")
    private String NAVER_LOGIN_REDIRECT_URl;
    @Value("${login.naver.client.secret}")
    private String NAVER_LOGIN_CLIENT_SECRET;
    @Value("${login.naver.login.token.url}")
    private String NAVER_LOGIN_LOGIN_TOKEN_URL;
    @Value("${login.naver.user.info.url}")
    private String NAVER_LOGIN_USER_INFO_URL;

    @Override
    public String getOauthRedirectURL(){
        //state 난수를 생성
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();

        //uri에 담을 부분 연결
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", NAVER_LOGIN_CLIENT_ID);
        params.put("redirect_uri", NAVER_LOGIN_REDIRECT_URl);
        params.put("state", state);

        // Redirect URL 생성
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return NAVER_LOGIN_BASE_URL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken(String code) throws JsonProcessingException{
        //JSON 파싱을 위한 기본 세팅
        ObjectMapper mapper = new ObjectMapper();
        //JSON -> Java Object, Token Request
        NaverTokenRes naverTokenRes;

        //네이버 로그인의 경우, 헤더 설정이 필요
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        //URI 빌더 사용
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(NAVER_LOGIN_LOGIN_TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", NAVER_LOGIN_CLIENT_ID)
                .queryParam("client_secret", NAVER_LOGIN_CLIENT_SECRET)
                .queryParam("code", code);


        //REST API 호출 이후 응답을 받을 때까지 기다리는 동기 방식
        RestTemplate restTemplate = new RestTemplate();

        //POST 요청 URI와 헤더를 같이 전송
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(uriComponentsBuilder.toUriString(),
                        HttpMethod.POST,
                        request,
                        String.class);

        //응답 객체 상태 반환
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            //JSON 객체 반환
            naverTokenRes = mapper.readValue(responseEntity.getBody(), NaverTokenRes.class);
            //응답 객체에서 real access token 추출
            return naverTokenRes.getAccess_token();
        }
        return "엑세스 토큰 발급 실패";
    }

    @SuppressWarnings("unchecked") //Map<String, Object> 사용 시 발생하는 경고 제거
    @Override
    public GetSocialUserRes getUserInfo(String accessToken){
        //네이버 로그인의 경우, 사용자 정보를 받아올 시에 필요
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-form-urlencoded;charset=utf-8");

        //헤더를 넣고, 객체를 구성
        HttpEntity request = new HttpEntity(headers);
        //REST API 호출 이후 응답을 받을 때까지 기다리는 동기 방식
        RestTemplate restTemplate = new RestTemplate();

        //POST 요청 URI와 헤더를 같이 전송
        ResponseEntity<JSONObject> responseEntity =
                    restTemplate.exchange(NAVER_LOGIN_USER_INFO_URL,
                            HttpMethod.POST,
                            request,
                            JSONObject.class);

        //객체 형태로 반환
        Map<String, Object> map = (Map<String, Object>)(Objects.requireNonNull(responseEntity.getBody())).get("response");
        String name = (String)map.get("name"); //이름을 받아올 경우, 유니코드 형식이지만 알아서 변환
        //이메일 정보 반환
        String email = (String)map.get("email");

        //공통 사용자 객체로 변환
        GetSocialUserRes socialUserRes = new GetSocialUserRes(name, email);

        //사용자 정보 추출
        System.out.println(">> naver user information :: {}" + socialUserRes);

        //사용자 정보 반환
        return socialUserRes;
    }

    @Override
    public String logout(){ return ""; }
}
