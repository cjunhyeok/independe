package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import community.independe.domain.token.RefreshToken;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.token.RefreshTokenRepository;
import community.independe.security.provider.JwtParser;
import community.independe.security.signature.SecuritySigner;
import community.independe.util.JwtTokenVerifier;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtTokenVerifier jwtTokenVerifier;
    @Mock
    private SecuritySigner securitySigner;
    @Mock
    private JWK jwk;
    @Mock
    private JwtParser jwtParser;

    @Test
    void saveTest() {
        // given
        String ip = "mockIp";
        String username = "username";
        Set<String> authorities = new HashSet<>();
        String token = "mockRefreshToken";
        RefreshToken refreshToken = RefreshToken.builder()
                .ip(ip)
                .authorities(authorities)
                .username(username)
                .refreshToken(token)
                .build();

        // stub
        when(refreshTokenRepository.findByUsername(username)).thenReturn(refreshToken);
        doNothing().when(refreshTokenRepository).deleteById(refreshToken.getId());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // when
        refreshTokenService.save(ip, authorities, token, username);

        // then
        verify(refreshTokenRepository, times(1)).findByUsername(username);
        verify(refreshTokenRepository, times(1)).deleteById(refreshToken.getId());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void saveNoInteractionDeleteTest() {
        // given
        String ip = "mockIp";
        String username = "username";
        Set<String> authorities = new HashSet<>();
        String token = "mockRefreshToken";

        // stub
        when(refreshTokenRepository.findByUsername(username)).thenReturn(null);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

        // when
        refreshTokenService.save(ip, authorities, token, username);

        // then
        verify(refreshTokenRepository, times(1)).findByUsername(username);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void reProvideRefreshTokenTest() throws JOSEException, ParseException {
        // given
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";
        RefreshToken mockRefreshToken = RefreshToken.builder().ip(currentIp).refreshToken(refreshToken).build();
        JWSHeader header = new JWSHeader.Builder(new JWSAlgorithm("SHA1")).build();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().build();
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(refreshTokenRepository.findByRefreshToken(anyString()))
                .thenReturn(mockRefreshToken);
        when(jwtParser.parse(mockRefreshToken.getRefreshToken())).thenReturn(signedJWT);
        when(jwtParser.getClaim(signedJWT, "username")).thenReturn("username");
        when(securitySigner.getRefreshJwtToken(anyString(), eq(jwk))).thenReturn("token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(RefreshToken.builder().build());

        // when
        refreshTokenService.reProvideRefreshToken(currentIp, refreshToken);

        // then
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(refreshTokenRepository, times(1)).findByRefreshToken(anyString());
        verify(jwtParser, times(1)).parse(mockRefreshToken.getRefreshToken());
        verify(jwtParser, times(1)).getClaim(signedJWT, "username");
        verify(securitySigner, times(1)).getRefreshJwtToken(anyString(), eq(jwk));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void reProvideRefreshTokenRefreshFailTest() throws JOSEException {
        // given
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(refreshTokenRepository.findByRefreshToken(anyString()))
                .thenReturn(null);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> refreshTokenService.reProvideRefreshToken(currentIp, refreshToken))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        });
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(refreshTokenRepository, times(1)).findByRefreshToken(anyString());
        verifyNoInteractions(jwtParser);
        verifyNoInteractions(securitySigner);
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void reProvideRefreshTokenIpFailTest() {
        // given
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";
        RefreshToken mockRefreshToken = RefreshToken.builder().ip("0.0.0.0").refreshToken(refreshToken).build();

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(refreshTokenRepository.findByRefreshToken(anyString()))
                .thenReturn(mockRefreshToken);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> refreshTokenService.reProvideRefreshToken(currentIp, refreshToken))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_IP_NOT_MATCH);
        });
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(refreshTokenRepository, times(1)).findByRefreshToken(anyString());
        verifyNoInteractions(jwtParser);
        verifyNoInteractions(securitySigner);
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void reProvideRefreshTokenParseFailTest() throws ParseException {
        // given
        String currentIp = "127.0.0.1";
        String refreshToken = "Bearer smockToken.body.claim; Secure; HttpOnly";
        RefreshToken mockRefreshToken = RefreshToken.builder().ip(currentIp).refreshToken(refreshToken).build();
        
        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(anyString());
        when(refreshTokenRepository.findByRefreshToken(anyString()))
                .thenReturn(mockRefreshToken);

        when(jwtParser.parse(mockRefreshToken.getRefreshToken())).thenThrow(ParseException.class);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> refreshTokenService.reProvideRefreshToken(currentIp, refreshToken))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        });
        verify(jwtTokenVerifier, times(1)).verifyToken(anyString());
        verify(refreshTokenRepository, times(1)).findByRefreshToken(anyString());
        verify(jwtParser, times(1)).parse(mockRefreshToken.getRefreshToken());
        verifyNoInteractions(securitySigner);
        verifyNoMoreInteractions(refreshTokenRepository);
    }
}
