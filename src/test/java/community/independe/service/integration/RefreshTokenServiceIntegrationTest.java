package community.independe.service.integration;

import com.nimbusds.jose.JOSEException;
import community.independe.IntegrationTestSupporter;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.MemberService;
import community.independe.service.RefreshTokenService;
import community.independe.service.dtos.JoinServiceDto;
import community.independe.service.dtos.LoginResponse;
import community.independe.service.dtos.LoginServiceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class RefreshTokenServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private SecuritySigner securitySigner;
    private final String RedisKeyPrefix = "refreshToken : ";

    @Test
    @DisplayName("ip, role, username 을 통해 리프래시 토큰을 저장한다.")
    void saveTest() {
        // given
        String ip = "ip";
        String role = "role";
        String username = "username";
        String refreshToken = "refreshToken";

        // when
        String savedUsername = refreshTokenService.save(ip, role, refreshToken, username);

        // then
        String findUsername = (String) redisTemplate.opsForHash().get(RedisKeyPrefix + username, "username");
        assertThat(findUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("리프래시 토큰을 재발급 한다.")
    void reProvideRefreshTokenTest() throws JOSEException {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto joinDto = createJoinDto(username, password, nickname);
        memberService.join(joinDto);

        String ip = "127.0.0.1";
        LoginServiceDto loginServiceDto = LoginServiceDto
                .builder()
                .username(username)
                .password(password)
                .ip(ip)
                .build();
        LoginResponse loginResponse = memberService.login(loginServiceDto);

        // when
        String refreshToken = refreshTokenService.reProvideRefreshToken(username, ip, "Bearer " + loginResponse.getRefreshToken());

        // then
        assertThat(refreshToken).isNotNull();
    }

    private JoinServiceDto createJoinDto(String username, String password, String nickname) {
        return JoinServiceDto
                .builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email("email")
                .number("number")
                .isPrivacyCheck(true)
                .isTermOfUseCheck(true)
                .build();
    }
}
