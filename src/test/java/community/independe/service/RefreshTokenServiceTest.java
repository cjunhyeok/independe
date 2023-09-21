package community.independe.service;

import community.independe.domain.token.RefreshToken;
import community.independe.repository.token.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

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
}
