package community.independe.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import community.independe.IntegrationTestSupporter;
import community.independe.domain.token.RefreshTokenMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class RefreshTokenRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void deleteData() {
        redisTemplate.delete("username");
    }

    @Test
    void saveTest() throws JsonProcessingException {
        // given
        String token = "refreshToken";
        String username = "username";
        String role = "role";
        String ip = "ip";
        Map<String, String> refreshToken = RefreshTokenMapper.refreshTokenMap(token, username, role, ip);

        // when
        redisTemplate.opsForHash().putAll(username, refreshToken);

        // then
        String findToken = (String) redisTemplate.opsForHash().get(username, "refreshToken");
        assertThat(findToken).isEqualTo(token);
    }

    @Test
    void deleteByIdTest() {
        // given
        String token = "refreshToken";
        String username = "username";
        String role = "role";
        String ip = "ip";
        Map<String, String> refreshToken = RefreshTokenMapper.refreshTokenMap(token, username, role, ip);
        redisTemplate.opsForHash().putAll(username, refreshToken);

        // when
        redisTemplate.delete(username);

        // then
        String findToken = (String) redisTemplate.opsForHash().get(username, "refreshToken");
        assertThat(findToken).isNull();
    }

    @Test
    void findByUsernameTest() {
        // given
        String token = "refreshToken";
        String username = "username";
        String role = "role";
        String ip = "ip";
        Map<String, String> refreshToken = RefreshTokenMapper.refreshTokenMap(token, username, role, ip);
        redisTemplate.opsForHash().putAll(username, refreshToken);

        // when
        String findUsername = (String) redisTemplate.opsForHash().get(username, username);

        // then
        assertThat(findUsername).isEqualTo(username);
    }

    @Test
    void findByUsernameFailTest() {
        // given
        String token = "refreshToken";
        String username = "username";
        String role = "role";
        String ip = "ip";
        Map<String, String> refreshToken = RefreshTokenMapper.refreshTokenMap(token, username, role, ip);
        redisTemplate.opsForHash().putAll(username, refreshToken);

        // when
        String findRefreshToken = (String) redisTemplate.opsForHash().get(username, "fail");

        // then
        assertThat(findRefreshToken).isNull();
    }
}
