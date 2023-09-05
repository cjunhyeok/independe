package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTParser;
import community.independe.domain.token.RefreshToken;
import community.independe.exception.RefreshTokenException;
import community.independe.repository.token.RefreshTokenRepository;
import community.independe.security.signature.SecuritySigner;
import community.independe.util.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final SecuritySigner securitySigner;
    private final JWK jwk;

    public String save(String ip, Set<String> authorities, String refreshToken) {

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);

        if (findRefreshToken != null) {
            refreshTokenRepository.deleteById(findRefreshToken.getId());
        }

        RefreshToken savedRefreshToken = saveRefreshToken(ip, authorities, refreshToken);

        return savedRefreshToken.getId();
    }

    public String reProvideRefreshToken(String currentIp, String refreshToken) throws JOSEException {

        RefreshToken findRefreshToken = RefreshTokenExceptionCheck(refreshToken, currentIp);
        return getAndSaveRefreshToken(findRefreshToken, currentIp);
    }

    private RefreshToken RefreshTokenExceptionCheck(String refreshToken, String currentIp) {

        String bearerToken = refreshToken.replace("; Secure; HttpOnly", "");
        jwtTokenVerifier.verifyToken(bearerToken);

        String sampleToken = bearerToken.replace("Bearer ", "");

        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(sampleToken);
        if (findRefreshToken == null) {
            throw new RefreshTokenException("Invalid RefreshToken");
        }

        String savedIp = findRefreshToken.getIp();
        if (!currentIp.equals(savedIp)) {
            throw new RefreshTokenException("Invalid RefreshToken Ip");
        }

        return findRefreshToken;
    }

    private String getAndSaveRefreshToken(RefreshToken findRefreshToken, String currentIp) throws JOSEException {
        String username;
        String newRefreshToken;
        try {
            username = (String) JWTParser.parse(findRefreshToken.getRefreshToken())
                    .getJWTClaimsSet().getClaim("username");

            newRefreshToken = securitySigner.getRefreshJwtToken(username, jwk);

            saveRefreshToken(currentIp, findRefreshToken.getAuthorities(), newRefreshToken);

        } catch (ParseException e) {
            throw new RefreshTokenException("Invalid RefreshToken");
        }

        return newRefreshToken;
    }

    private RefreshToken saveRefreshToken(String currentIp, Set<String> authorities, String refreshToken) {

        RefreshToken token = RefreshToken.builder()
                .ip(currentIp)
                .authorities(authorities)
                .refreshToken(refreshToken)
                .build();
        return refreshTokenRepository.save(token);
    }
}
