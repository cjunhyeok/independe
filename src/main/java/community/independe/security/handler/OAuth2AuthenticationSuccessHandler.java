package community.independe.security.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.security.signature.SecuritySigner;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SecuritySigner securitySigner;
    private final JWK jwk;

    public OAuth2AuthenticationSuccessHandler(SecuritySigner securitySigner, JWK jwk) {
//        super(new AntPathRequestMatcher("/api/login"));
        this.securitySigner = securitySigner;
        this.jwk = jwk;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String jwtToken;

        try {
            jwtToken = securitySigner.getOAuth2JwtToken(oAuth2User, jwk);
            response.addHeader("Authorization", "Bearer " + jwtToken);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
