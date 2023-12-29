package community.independe.domain.token;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenMapper {

    public static Map<String, String> refreshTokenMap(String refreshToken, String username, String role, String ip) {
        Map<String, String> map = new HashMap<>();
        map.put("refreshToken", refreshToken);
        map.put("username", username);
        map.put("role", role);
        map.put("ip", ip);

        return map;
    }
}
