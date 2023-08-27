package community.independe.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface RefreshTokenService {
    String save(String ip, Collection<GrantedAuthority> authorities, String refreshToken);
}
