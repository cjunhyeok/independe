package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.domain.token.RefreshTokenMapper;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.security.signature.SecuritySigner;
import community.independe.util.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtTokenVerifier jwtTokenVerifier;
    private final SecuritySigner securitySigner;
    private final JWK jwk;
    private final RedisTemplate redisTemplate;

    @Override
    @Transactional
    public String save(String ip, String role, String refreshToken, String username) {

        String findRefreshToken = (String) redisTemplate.opsForHash().get(username, "refreshToken");

        if (findRefreshToken != null) {
            redisTemplate.delete(username);
        }

        Map<String, String> stringStringMap = saveRefreshToken(ip, role, refreshToken, username);
        return stringStringMap.get("username");
    }

    @Override
    @Transactional
    public String reProvideRefreshToken(String username, String currentIp, String refreshToken) throws JOSEException {

        String bearerToken = refreshToken.replace("; Secure; HttpOnly", "");
        jwtTokenVerifier.verifyToken(bearerToken);

        String findRefreshToken = (String) redisTemplate.opsForHash().get(username, "refreshToken");
        if (findRefreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        String savedIp = (String) redisTemplate.opsForHash().get(username, "ip");
        if (!currentIp.equals(savedIp)) {
            throw new CustomException(ErrorCode.REFRESH_IP_NOT_MATCH);
        }

        String role = (String) redisTemplate.opsForHash().get(username, "role");
        String newRefreshToken = securitySigner.getRefreshJwtToken(username, jwk);
        saveRefreshToken(currentIp, role, newRefreshToken, username);

        return newRefreshToken;
    }

    private Map<String, String> saveRefreshToken(String currentIp, String role, String refreshToken, String username) {

        Map<String, String> refreshTokenMap = RefreshTokenMapper.refreshTokenMap(refreshToken, username, role, currentIp);
        redisTemplate.opsForHash().putAll(username, refreshTokenMap);
        return refreshTokenMap;
    }
}
