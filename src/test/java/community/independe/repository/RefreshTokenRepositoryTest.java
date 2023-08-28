package community.independe.repository;

import community.independe.domain.token.RefreshToken;
import community.independe.repository.token.RefreshTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    public void deleteData() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    void saveTest() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .ip("mockIp")
                .refreshToken("mockRefreshToken")
                .build();

        // when
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        RefreshToken findRefreshToken = refreshTokenRepository.findById(savedRefreshToken.getId()).orElseThrow(
                () -> new IllegalArgumentException()
        );

        // then
        assertThat(findRefreshToken.getId()).isEqualTo(savedRefreshToken.getId());
    }

    @Test
    void findByRefreshTokenTest() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .ip("mockIp")
                .refreshToken("mockRefreshToken")
                .build();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // when
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken("mockRefreshToken");

        // then
        assertThat(findRefreshToken.getId()).isEqualTo(savedRefreshToken.getId());
        assertThat(findRefreshToken.getRefreshToken()).isEqualTo(savedRefreshToken.getRefreshToken());
    }
}
