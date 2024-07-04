package community.independe.service.integration;

import com.nimbusds.jose.JOSEException;
import community.independe.IntegrationTestSupporter;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.MemberService;
import community.independe.service.RefreshTokenService;
import community.independe.service.dtos.JoinServiceDto;
import community.independe.service.dtos.LoginResponse;
import community.independe.service.dtos.LoginServiceDto;
import org.assertj.core.api.AbstractObjectAssert;
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

    @Test
    @DisplayName("기존에 저장된 토큰이 없다면 예외가 발생한다.")
    void reProvideRefreshTokenFindFailTest() throws JOSEException {
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
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() ->
                refreshTokenService
                        .reProvideRefreshToken(username + "1", ip, "Bearer " + loginResponse.getRefreshToken()))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        });
    }
    
    @Test
    @DisplayName("기존에 저장된 토큰과 ip가 일치하지 않다면 예외가 발생한다.")
    void reProvideRefreshTokenIpFailTest() {
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
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() ->
                refreshTokenService
                        .reProvideRefreshToken(username, ip + "1", "Bearer " + loginResponse.getRefreshToken()))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REFRESH_IP_NOT_MATCH);
        });
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
