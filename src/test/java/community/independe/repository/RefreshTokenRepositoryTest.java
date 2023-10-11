package community.independe.repository;

import community.independe.domain.token.RefreshToken;
import community.independe.repository.token.RefreshTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

//@Disabled
@SpringBootTest
@AutoConfigureMockMvc
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
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        RefreshToken refreshToken = RefreshToken.builder()
                .ip("mockIp")
                .refreshToken("mockRefreshToken")
                .username("mockUsername")
                .authorities(roles)
                .build();

        // when
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        RefreshToken findRefreshToken = refreshTokenRepository.findById(savedRefreshToken.getId()).orElseThrow(
                () -> new IllegalArgumentException()
        );

        // then
        assertThat(findRefreshToken.getId()).isEqualTo(savedRefreshToken.getId());
        assertThat(findRefreshToken.getRefreshToken()).isEqualTo(savedRefreshToken.getRefreshToken());
        assertThat(findRefreshToken.getUsername()).isEqualTo(savedRefreshToken.getUsername());
        assertThat(findRefreshToken.getAuthorities()).isEqualTo(savedRefreshToken.getAuthorities());
        assertThat(findRefreshToken.getIp()).isEqualTo(savedRefreshToken.getIp());
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

    @Test
    void deleteByIdTest() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .ip("mockIp")
                .refreshToken("mockRefreshToken")
                .build();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteById(refreshToken.getId());

        // then
        assertThat(refreshTokenRepository.findById(refreshToken.getId())).isEmpty();
    }

    @Test
    void findByUsernameTest() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .username("username")
                .ip("mockIp")
                .refreshToken("mockRefreshToken")
                .build();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // when
        RefreshToken findRefreshToken = refreshTokenRepository.findByUsername("username");

        // then
        assertThat(findRefreshToken.getIp()).isEqualTo(savedRefreshToken.getIp());
    }

    @Test
    void findByUsernameFailTest() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .username("username")
                .ip("mockIp")
                .refreshToken("mockRefreshToken")
                .build();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // when
        RefreshToken findRefreshToken = refreshTokenRepository.findByUsername("fail");

        // then
        assertThat(findRefreshToken).isNull();
    }
}
