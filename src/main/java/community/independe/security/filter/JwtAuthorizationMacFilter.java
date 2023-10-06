package community.independe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.exception.CustomException;
import community.independe.util.UrlList;
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
            String requestURI = request.getRequestURI();
            boolean isBlackListed = checkBlackList(requestURI);

            if (isBlackListed) {
                jwtTokenVerifier.verifyToken(request);
                filterChain.doFilter(request, response);
            } else {
                filterChain.doFilter(request, response);
            }

        } catch (CustomException e) {
            makeExceptionMessage(response, e.getErrorCode().getErrorMessage(), e);
        }
    }

    private boolean checkBlackList(String requestURI) {
        boolean isBlackListed = false;

        for (String blackList : UrlList.getBlackList()) {
            if (isPatternMatch(requestURI, blackList)) {
                isBlackListed = true;
                break;
            }
        }

        return isBlackListed;
    }

    private boolean isPatternMatch(String url, String pattern) {
        // 패턴이 일치하면 true 반환, **를 정규 표현식으로 처리
        return url.equals(pattern) || url.matches(pattern.replace("**", ".*"))
                || (pattern.endsWith("/**") && url.startsWith(pattern.substring(0, pattern.length() - 3)));
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
