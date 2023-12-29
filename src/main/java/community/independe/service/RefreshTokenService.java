package community.independe.service;

import com.nimbusds.jose.JOSEException;

import java.util.Set;

public interface RefreshTokenService {
    String save(String ip, String role, String refreshToken, String username);

    String reProvideRefreshToken(String username, String currentIp, String refreshToken) throws JOSEException;
}
