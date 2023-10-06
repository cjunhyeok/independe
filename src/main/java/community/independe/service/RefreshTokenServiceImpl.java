package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWT;
import community.independe.domain.token.RefreshToken;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.token.RefreshTokenRepository;
import community.independe.security.provider.JwtParser;
import community.independe.security.signature.SecuritySigner;
import community.independe.util.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final SecuritySigner securitySigner;
    private final JWK jwk;
    private final JwtParser jwtParser;

    @Override
    public String save(String ip, Set<String> authorities, String refreshToken, String username) {

        RefreshToken findRefreshToken = refreshTokenRepository.findByUsername(username);

        if (findRefreshToken != null) {
            refreshTokenRepository.deleteById(findRefreshToken.getId());
        }

        RefreshToken savedRefreshToken = saveRefreshToken(ip, authorities, refreshToken, username);

        return savedRefreshToken.getId();
    }

    @Override
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
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        String savedIp = findRefreshToken.getIp();
        if (!currentIp.equals(savedIp)) {
            throw new CustomException(ErrorCode.REFRESH_IP_NOT_MATCH);
        }

        return findRefreshToken;
    }

    private String getAndSaveRefreshToken(RefreshToken findRefreshToken, String currentIp) throws JOSEException {
        String username;
        String newRefreshToken;
        try {

            JWT parsedJwt = jwtParser.parse(findRefreshToken.getRefreshToken());
            username = jwtParser.getClaim(parsedJwt, "username");

            newRefreshToken = securitySigner.getRefreshJwtToken(username, jwk);

            saveRefreshToken(currentIp, findRefreshToken.getAuthorities(), newRefreshToken, username);

        } catch (ParseException e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        return newRefreshToken;
    }

    private RefreshToken saveRefreshToken(String currentIp, Set<String> authorities, String refreshToken, String username) {

        RefreshToken token = RefreshToken.builder()
                .ip(currentIp)
                .authorities(authorities)
                .refreshToken(refreshToken)
                .username(username)
                .build();
        return refreshTokenRepository.save(token);
    }
}
