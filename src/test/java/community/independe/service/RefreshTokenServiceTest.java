package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.security.signature.SecuritySigner;
import community.independe.util.JwtTokenVerifier;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;
    @Mock
    private JwtTokenVerifier jwtTokenVerifier;
    @Mock
    private SecuritySigner securitySigner;
    @Mock
    private JWK jwk;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private HashOperations hashOperations;
    private final String RedisKeyPrefix = "refreshToken : ";

    @Test
    void saveTest() {
        // given
        String ip = "mockIp";
        String username = "username";
        String role = "ROLE_USER";
        String token = "mockRefreshToken";

        // stub
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get( RedisKeyPrefix + username, "refreshToken")).thenReturn(token);
        when(redisTemplate.delete(RedisKeyPrefix + username)).thenReturn(true);
        doNothing().when(hashOperations).putAll(anyString(), anyMap());

        // when
        refreshTokenService.save(ip, role, token, username);

        // then
        verify(redisTemplate, times(2)).opsForHash();
        verify(hashOperations, times(1)).get( RedisKeyPrefix + username, "refreshToken");
        verify(redisTemplate, times(1)).delete(RedisKeyPrefix + username);
        verify(hashOperations, times(1)).putAll(anyString(), anyMap());
    }

    @Test
    void saveNoInteractionDeleteTest() {
        String ip = "mockIp";
        String username = "username";
        String role = "ROLE_USER";
        String token = "mockRefreshToken";

        // stub
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get( RedisKeyPrefix + username, "refreshToken")).thenReturn(null);
        doNothing().when(hashOperations).putAll(anyString(), anyMap());

        // when
        refreshTokenService.save(ip, role, token, username);

        // then
        verify(redisTemplate, times(2)).opsForHash();
        verify(hashOperations, times(1)).get( RedisKeyPrefix + username, "refreshToken");
        verify(hashOperations, times(1)).putAll(anyString(), anyMap());
    }

    @Test
    void reProvideRefreshTokenTest() throws JOSEException, ParseException {
        // given
        String username = "username";
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get( RedisKeyPrefix + username, "refreshToken")).thenReturn(refreshToken);
        when(hashOperations.get( RedisKeyPrefix + username, "ip")).thenReturn(currentIp);
        when(hashOperations.get( RedisKeyPrefix + username, "role")).thenReturn("ROLE_USER");
        when(securitySigner.getRefreshJwtToken(anyString(), eq(jwk))).thenReturn("token");
        doNothing().when(hashOperations).putAll(anyString(), anyMap());

        // when
        refreshTokenService.reProvideRefreshToken(username, currentIp, refreshToken);

        // then
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(redisTemplate, times(4)).opsForHash();
        verify(hashOperations, times(1)).get( RedisKeyPrefix + username, "refreshToken");
        verify(hashOperations, times(1)).get( RedisKeyPrefix + username, "ip");
        verify(hashOperations, times(1)).get( RedisKeyPrefix + username, "role");
        verify(securitySigner, times(1)).getRefreshJwtToken(anyString(), eq(jwk));
        verify(hashOperations, times(1)).putAll(anyString(), anyMap());
    }

    @Test
    void reProvideRefreshTokenRefreshFailTest() throws JOSEException {
        // given
        String username = "username";
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get( RedisKeyPrefix + username, "refreshToken")).thenReturn(null);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> refreshTokenService.reProvideRefreshToken(username, currentIp, refreshToken))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        });
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(redisTemplate, times(1)).opsForHash();
        verify(hashOperations, times(1)).get( RedisKeyPrefix + username, "refreshToken");
        verifyNoInteractions(securitySigner);
        verifyNoMoreInteractions(redisTemplate);
        verifyNoMoreInteractions(hashOperations);
    }

    @Test
    void reProvideRefreshTokenIpFailTest() {
        // given
        String username = "username";
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get( RedisKeyPrefix + username, "refreshToken")).thenReturn(refreshToken);
        when(hashOperations.get( RedisKeyPrefix + username, "ip")).thenReturn("failIp");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> refreshTokenService.reProvideRefreshToken(username, currentIp, refreshToken))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_IP_NOT_MATCH);
        });
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(redisTemplate, times(2)).opsForHash();
        verify(hashOperations, times(1)).get(RedisKeyPrefix + username, "refreshToken");
        verify(hashOperations, times(1)).get(RedisKeyPrefix + username, "ip");
        verifyNoInteractions(securitySigner);
        verifyNoMoreInteractions(redisTemplate);
        verifyNoMoreInteractions(hashOperations);
    }
}
