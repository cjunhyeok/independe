package community.independe.security.service;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class CustomUserDetailsServiceTest extends IntegrationTestSupporter {

    @Autowired
    private UserDetailsService customUserDetailsService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사용자 username 으로 조회해 MemberContext 를 생성한다.")
    void loadUserByUsernameTest() {
        // given
        String username = "username";
        String nickname = "nickname";
        Member savedMember = createMember(username, nickname);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // then
        MemberContext memberContext = (MemberContext) userDetails;
        assertThat(memberContext.getMemberId()).isEqualTo(savedMember.getId());
        assertThat(memberContext.getUsername()).isEqualTo(savedMember.getUsername());
    }

    @Test
    @DisplayName("사용자 username 을 잘못 입력할 시 예외가 발생한다.")
    void loadUserByUsernameFailTest() {
        // given
        String username = "username";
        String nickname = "nickname";
        Member savedMember = createMember(username, nickname);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(username + "fail"))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_USERNAME);
        });
    }

    private Member createMember(String username, String nickname) {
        Member member = Member
                .builder()
                .username(username)
                .password("password")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();
        return memberRepository.save(member);
    }
}
