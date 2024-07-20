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
            String url = request.getRequestURI();
            String method = request.getMethod();
            boolean isBlackListed = checkBlackList(url, method);

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

    private boolean checkBlackList(String url, String method) {
        if (method == null) {
            return false;
        }

        String[] blackListUrls = UrlList.getBlackList().get(method);

        if (blackListUrls == null) {
            return false;
        }

        for (String urlPattern : blackListUrls) {
            if (matchUrlPattern(urlPattern, url)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchUrlPattern(String urlPattern, String requestURI) {
        if (urlPattern.endsWith("/**")) {
            String basePattern = urlPattern.substring(0, urlPattern.length() - 3);
            return requestURI.startsWith(basePattern);
        } else {
            return urlPattern.equals(requestURI);
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
