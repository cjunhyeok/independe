package community.independe.domain.token;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Set;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 604800)
public class RefreshToken {

    @Id
    private String id;
    private String ip;
    private Set<String> authorities;
    @Indexed
    private String refreshToken;
    @Indexed
    private String username;

    @Builder
    public RefreshToken(String ip, Set<String> authorities, String refreshToken, String username) {
        this.ip = ip;
        this.authorities = authorities;
        this.refreshToken = refreshToken;
        this.username = username;
    }
}
