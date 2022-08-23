package retoon.retoon_server.src.login.token;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import retoon.retoon_server.config.secret.Secret;

import javax.annotation.PostConstruct;
import javax.security.auth.message.config.AuthConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

// 엑세스 토큰을 발행해줄 수 있는 Provider
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private String secretKey = Secret.JWT_SECRET_KEY; // JWT 비밀키

    private long tokenValidTime = 1000L * 60 * 30; // 30분, 만료기간 설정
    private long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7; // 7일, 만료기간 설정

    private final UserDetailsService memberDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 생성 함수
    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 리프레시 토큰 생성 함수
    public String createRefreshToken() {
        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰으로 인증 객체를 얻기 위한 메소드
    public Authentication getAuthentication(String token){
        UserDetails userDetails = memberDetailsService.loadUserByUsername(getMemberEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 이메일을 얻기 위해 토큰을 해독
    public String getMemberEmail(String token){
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        } catch(ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    // 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest req) {
        return req.getHeader("X-AUTH-TOKEN");
    }

    // 토큰 만료 확인
    public boolean validateTokenExpiration(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch(Exception e) {
            return false;
        }
    }
}
