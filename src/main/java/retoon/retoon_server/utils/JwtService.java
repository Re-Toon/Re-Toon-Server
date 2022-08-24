package retoon.retoon_server.utils;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.secret.Secret;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

import static retoon.retoon_server.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class JwtService {

    private String secretKey = Secret.JWT_SECRET_KEY; // JWT 비밀키

    // private long tokenValidTime = 1000L * 60 * 30; // 30분, 만료기간 설정
    private long tokenValidTime = 1000L * 60L * 60L * 24L * 365L; // 우선적으로 길게 설정
    private long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7; // 7일, 만료기간 설정

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 바이트 형태로 변환
    // Key key = Keys.hmacShaKeyFor(Secret.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    // 제대로 작동하지 않을 경우 key 전부 Secret.JWT_SECRET_KEY 로 변경, signWith 앞뒤 순서 변경

    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(int userIdx){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx", userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+ tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /*
    * refreshJWT 생성
    * @return String
    * */
    public String createRefreshJwt() {
        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        //헤더에서 Jwt를 받아오는 함수
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException {
        //1. JWT 추출
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);


    }

    // 토큰의 유효성, 만료일자 확인
    public boolean verifyJwt(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
