package retoon.retoon_server.src.login.handler;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 인증되지 않았을 경우(비로그인) AuthenticationEntryPoint 부분에서 AuthenticationException 발생시키면서
 * 비로그인 상태에서 인증 실패 시, AuthenticationEntryPoint 로 핸들링되어 이곳에서 처리
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendRedirect("/exception/entry");
    }
}
