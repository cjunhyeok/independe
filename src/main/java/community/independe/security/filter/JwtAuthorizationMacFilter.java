package community.independe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.security.exception.JwtNotFoundException;
import community.independe.security.exception.JwtVerifyException;
import community.independe.util.JwtTokenVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// OncePerRequestFilter : 요청에 대해 한번만 실행
@Slf4j
public class JwtAuthorizationMacFilter extends OncePerRequestFilter {

    private final JwtTokenVerifier jwtTokenVerifier;

    public JwtAuthorizationMacFilter(JwtTokenVerifier jwtTokenVerifier) {
        this.jwtTokenVerifier = jwtTokenVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            jwtTokenVerifier.verifyToken(request);
        } catch (RuntimeException e) {
            if (e instanceof JwtNotFoundException) {
                makeExceptionMessage(response, "토큰을 찾을 수 없습니다.", e);
            } else if (e instanceof JwtVerifyException) {
                makeExceptionMessage(response, "토큰 검증에 실패하였습니다.", e);
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    private void makeExceptionMessage(HttpServletResponse response, String message, Exception e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("message", message);
        body.put("exceptionMessage", e.getMessage());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
