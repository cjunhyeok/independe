package community.independe.security.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import community.independe.security.signature.SecuritySigner;
import community.independe.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SecuritySigner securitySigner;
    private final JWK jwk;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
//    private List<String> authorizedRedirectUris = new ArrayList<>();

    public OAuth2AuthenticationSuccessHandler(SecuritySigner securitySigner, JWK jwk, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
//        super(new AntPathRequestMatcher("/api/login"));
        this.securitySigner = securitySigner;
        this.jwk = jwk;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
//        this.authorizedRedirectUris.add("http://vue:8081/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        List<String> authority = oAuth2User.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String role = authority.get(0);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
//
//        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
////            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//        }

        List<String> authority = oAuth2User.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String role = authority.get(0);

        log.info("in success");

        String targetUrl = null;
        if (role.equals("ROLE_USER")) {
//            targetUrl = "http://localhost:8081";
            targetUrl = "https://www.independe.co.kr";
            String token;
            try {
                token = securitySigner.getOAuth2JwtToken(oAuth2User, jwk);

//            response.addHeader("Authorization", "Bearer " + token);
//            log.info(token);
                return UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("token", token)
                        .build().toUriString();

            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        } else {
            targetUrl = redirectUri.orElse(getDefaultTargetUrl());
            String token;
            try {
                token = securitySigner.getOAuth2JwtToken(oAuth2User, jwk);

//            response.addHeader("Authorization", "Bearer " + token);
//            log.info(token);
                return UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("token", token)
                        .build().toUriString();

            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

//    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//
//        return authorizedRedirectUris
//                .stream()
//                .anyMatch(authorizedRedirectUri -> {
//                    // Only validate host and port. Let the clients use different paths if they want to
//                    URI authorizedURI = URI.create(authorizedRedirectUri);
//                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
//                        return true;
//                    }
//                    return false;
//                });
//    }
}
