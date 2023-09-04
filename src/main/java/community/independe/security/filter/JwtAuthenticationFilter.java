package community.independe.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.domain.member.Member;
import community.independe.security.filter.dto.LoginDto;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        Member member = (Member) authResult.getPrincipal();
        String username = member.getUsername();
        String jwtToken;
        String refreshToken;
        String ip = request.getRemoteAddr();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole());
        authorities.add(grantedAuthority);

        try {
            jwtToken = securitySigner.getJwtToken(username, jwk);
            refreshToken = securitySigner.getRefreshJwtToken(username, jwk);
            refreshTokenService.save(ip, authorities, refreshToken);
            response.addHeader("Authorization", "Bearer " + jwtToken);
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(refreshTokenCookie);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (failed instanceof BadCredentialsException) {
            makeExceptionMessage(response, "잘못된 비밀번호입니다.", failed);
        } else if (failed instanceof UsernameNotFoundException) {
            makeExceptionMessage(response, "잘못된 아이디입니다.", failed);
        } else {
            super.unsuccessfulAuthentication(request, response, failed);
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
