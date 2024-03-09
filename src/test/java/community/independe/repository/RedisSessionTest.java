package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisSessionTest extends IntegrationTestSupporter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void deleteData() {
        redisTemplate.delete("key");
    }

    @Test
    void setAddTest() {
        // given
        String key = "key";
        String firstValue = "firstValue";
        String secondValue = "secondValue";

        // when
        redisTemplate.opsForSet().add(key, firstValue, secondValue);

        // then
        Set<String> values = redisTemplate.opsForSet().members(key);
        assertThat(values.size()).isEqualTo(2);
    }

    @Test
    void setRemoveTest() {
        // given
        String key = "key";
        String value = "value";
        redisTemplate.opsForSet().add(key, value);

        // when
        redisTemplate.opsForSet().remove(key, value);

        // then
        Set<String> members = redisTemplate.opsForSet().members(key);
        assertThat(members.isEmpty()).isTrue();
    }

    @Test
    void stringSetTest() {
        // given
        String key = "key";
        String value = "value";

        // when
        redisTemplate.opsForValue().set(key, value);

        // then
        String findValue = redisTemplate.opsForValue().get(key);
        assertThat(findValue).isEqualTo(value);
    }

    @Test
    void stringDeleteTest() {
        // given
        String key = "key";
        String value = "value";
        redisTemplate.opsForValue().set(key, value);

        // when
        redisTemplate.opsForValue().getOperations().delete(key);

        // then
        String findValue = redisTemplate.opsForValue().get(key);
        assertThat(findValue).isNull();
    }
}
