package community.independe.service;

import community.independe.domain.token.RefreshToken;
import community.independe.repository.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    public String save(String ip, Collection<GrantedAuthority> authorities, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .ip(ip)
                .authorities(authorities)
                .refreshToken(refreshToken)
                .build();

        RefreshToken savedRefreshToken = refreshTokenRepository.save(token);
        return savedRefreshToken.getId();
    }
}
