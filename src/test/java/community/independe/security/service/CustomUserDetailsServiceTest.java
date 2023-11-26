package community.independe.security.service;

import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void loadUserByUsernameTest() {
        // given
        String username = "username";
        String role = "ROLE_USER";
        String password = "password";
        Member member = Member.builder().username(username).password(password).role(role).build();

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(member);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // then
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test
    void loadUserByUsernameFailTest() {
        // given
        String username = "username";
        String role = "ROLE_USER";
        String password = "password";
        Member member = Member.builder().username(username).password(password).role(role).build();

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(null);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(username))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_USERNAME);
        });
    }

    @Test
    void memberContextTest() {
        // given
        String username = "username";
        String role = "ROLE_USER";
        String password = "password";
        Member member = Member.builder().username(username).password(password).role(role).build();
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(member.getRole()));
        Map<String, Object> attributes = new HashMap<>();

        // when
        MemberContext memberContext = new MemberContext(member, roles, attributes);

        // then
        assertThat(memberContext.getMember()).isEqualTo(member);
        assertThat(memberContext.getPassword()).isEqualTo(member.getPassword());
        assertThat(memberContext.getAttributes()).isEqualTo(attributes);
        assertThat(memberContext.getName()).isNull();


    }
}
