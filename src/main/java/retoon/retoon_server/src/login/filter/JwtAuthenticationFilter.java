package retoon.retoon_server.src.login.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import retoon.retoon_server.src.login.token.JwtTokenProvider;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// 발급 받은 토큰을 기반으로 이를 처리하는 필터를 구성
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request); // 헤더에서 토큰을 추출

        if (token != null && jwtTokenProvider.validateTokenExpiration(token)) { // 토큰이 존재하는지, 만료기간이 지나지는 않았는지 확인
            Authentication auth = jwtTokenProvider.getAuthentication(token); // 성공할 경우, 인증 객체를 받아옴
            SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContextHolder 저장, 인증이 가능하도록 설정
        }
        chain.doFilter(request, response); // 다음 필터로 넘어가서 실제 AuthenticationFilter 인증된 객체를 통해 인증 되도록 구성
    }
}
