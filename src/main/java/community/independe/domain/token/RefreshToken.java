package community.independe.domain.token;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@RedisHash(value = "refresh", timeToLive = 604800)
public class RefreshToken {

    @Id @GeneratedValue
    private String id;
    private String ip;
    private Collection<? extends GrantedAuthority> authorities;
    @Indexed
    private String refreshToken;

    @Builder
    public RefreshToken(String ip, Collection<? extends GrantedAuthority> authorities, String refreshToken) {
        this.ip = ip;
        this.authorities = authorities;
        this.refreshToken = refreshToken;
    }
}
