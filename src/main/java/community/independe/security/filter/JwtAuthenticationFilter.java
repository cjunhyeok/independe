package community.independe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.security.filter.dto.LoginDto;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final RefreshTokenService refreshTokenService;
    private final SecuritySigner securitySigner;
    private final JWK jwk;

    public JwtAuthenticationFilter(SecuritySigner securitySigner, JWK jwk, RefreshTokenService refreshTokenService) {
        super(new AntPathRequestMatcher("/api/login"));
        this.refreshTokenService = refreshTokenService;
        this.securitySigner = securitySigner;
        this.jwk = jwk;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = null;

        try {
            loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        User user = (User) authResult.getPrincipal();
        String jwtToken;
        String refreshToken;
        String ip = request.getRemoteAddr();

        try {
            jwtToken = securitySigner.getJwtToken(user, jwk);
            refreshToken = securitySigner.getRefreshToken(user, jwk);
            refreshTokenService.save(ip, user.getAuthorities(), refreshToken);
            response.addHeader("Authorization", "Bearer " + jwtToken);
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(refreshTokenCookie);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
