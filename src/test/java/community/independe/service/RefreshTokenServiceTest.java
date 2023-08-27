package community.independe.service;

import community.independe.domain.token.RefreshToken;
import community.independe.repository.token.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void saveTest() {
        // given
        String ip = "mockIp";
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        String token = "mockRefreshToken";
        RefreshToken refreshToken = RefreshToken.builder()
                .ip(ip)
                .authorities(authorities)
                .refreshToken(token)
                .build();

        // when
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        refreshTokenService.save(ip, authorities, token);

        // then
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }
}
