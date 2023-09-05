package community.independe.service;

import com.nimbusds.jose.JOSEException;

import java.util.Set;

public interface RefreshTokenService {
    String save(String ip, Set<String> authorities, String refreshToken, String username);

    String reProvideRefreshToken(String currentIp, String refreshToken) throws JOSEException;
}
