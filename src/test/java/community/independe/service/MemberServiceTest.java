package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.dtos.JoinServiceDto;
import community.independe.service.dtos.LoginResponse;
import community.independe.service.dtos.LoginServiceDto;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecuritySigner securitySigner;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JWK jwk;

    @Test
    public void joinTest() {
        // given
        JoinServiceDto joinServiceDto = createJoinServiceDto();

        // stub
        when(memberRepository.findByUsername(joinServiceDto.getUsername())).thenReturn(null);
        when(memberRepository.findByNickname(joinServiceDto.getNickname())).thenReturn(null);
        when(passwordEncoder.encode(joinServiceDto.getPassword())).thenReturn("hashedPassword");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            setPrivateField(member, "id", 1L);  // Reflection으로 ID 값을 설정
            return member;
        });

        // when
        Long joinMemberId = memberService.join(joinServiceDto);

        verify(memberRepository).findByUsername(joinServiceDto.getUsername());
        verify(passwordEncoder).encode(joinServiceDto.getPassword());
        verify(memberRepository).save(any(Member.class));
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    void joinCheckUsernameFailTest() {
        // given
        JoinServiceDto joinServiceDto = createJoinServiceDto();

        // stub
        when(memberRepository.findByUsername(joinServiceDto.getUsername())).thenReturn(Member.builder().build());

        // when
        AbstractThrowableAssert<?, ? extends Throwable> abstractThrowableAssert =
                assertThatThrownBy(() -> memberService.join(joinServiceDto));

        // then
        abstractThrowableAssert
                .isInstanceOf(CustomException.class);
    }

    @Test
    void joinCheckNicknameFailTest() {
        // given
        JoinServiceDto joinServiceDto = createJoinServiceDto();

        // stub
        when(memberRepository.findByUsername(joinServiceDto.getUsername())).thenReturn(null);
        when(memberRepository.findByNickname(joinServiceDto.getNickname())).thenReturn(Member.builder().build());

        // when
        AbstractThrowableAssert<?, ? extends Throwable> abstractThrowableAssert =
                assertThatThrownBy(() -> memberService.join(joinServiceDto));

        // then
        abstractThrowableAssert
                .isInstanceOf(CustomException.class);
    }

    private JoinServiceDto createJoinServiceDto() {
        return JoinServiceDto.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .isPrivacyCheck(true)
                .isPrivacyCheck(true)
                .build();
    }

    @Test
    void loginTest() throws JOSEException {
        // given
        LoginServiceDto loginServiceDto = createLoginServiceDto();
        String username = loginServiceDto.getUsername();
        String password = loginServiceDto.getPassword();
        String ip = loginServiceDto.getIp();
        String role = "ROLE_USER";
        Member member = Member
                .builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        String jwtToken = "jwtToken";
        String refreshToken = "refreshToken";

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(member);
        when(passwordEncoder.matches(password,password)).thenReturn(true);
        when(securitySigner.getJwtToken(username, jwk)).thenReturn(jwtToken);
        when(securitySigner.getRefreshJwtToken(username, jwk)).thenReturn(refreshToken);
        when(refreshTokenService.save(ip, role, refreshToken, username)).thenReturn(refreshToken);

        // when
        LoginResponse loginResponse = memberService.login(loginServiceDto);

        // then
        assertThat(loginResponse.getAccessToken()).isEqualTo(jwtToken);
        assertThat(loginResponse.getRefreshToken()).isEqualTo(refreshToken);
    }

    private LoginServiceDto createLoginServiceDto() {
        return LoginServiceDto
                .builder()
                .username("username")
                .password("password")
                .ip("ip")
                .build();
    }

    @Test
    void loginUsernameFailTest() {
        // given
        String username = "username";
        String password = "password";
        String ip = "ip";

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(null);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> memberService.login(username, password, ip))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_USERNAME);
        });
        verify(memberRepository, times(1)).findByUsername(username);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(securitySigner);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void loginPasswordFailTest() {
        // given
        String username = "username";
        String password = "password";
        String ip = "ip";
        Member member = Member.builder().username(username).role("ROLE_USER").password(password).build();

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(member);
        when(passwordEncoder.matches(password, password)).thenReturn(false);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> memberService.login(username, password, ip))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
        });
        verify(memberRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, password);
        verifyNoInteractions(securitySigner);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void loginTokenFailTest() throws JOSEException {
        // given
        String username = "username";
        String password = "password";
        String ip = "ip";
        Member member = Member.builder().username(username).role("ROLE_USER").password(password).build();
        Set<String> authorities = new HashSet<>();
        authorities.add(member.getRole());
        String jwtToken = "jwtToken";
        String refreshToken = "refreshToken";

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(member);
        when(passwordEncoder.matches(password, password)).thenReturn(true);
        when(securitySigner.getJwtToken(username, jwk)).thenThrow(JOSEException.class);
        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> memberService.login(username, password, ip))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        });
        verify(memberRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, password);
        verify(securitySigner, times(1)).getJwtToken(username, jwk);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void modifyOAuthMemberTest() {
        // given
        Long memberId = 1L;
        String nickname = "modifyNickname";
        String email = "modifyEmail";
        String number = "01064613134";

        Member mockMember = Member.builder().build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        memberService.modifyOAuthMember(memberId, nickname, email, number);

        // then
        verify(memberRepository).findById(memberId);
        assertThat(mockMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    void modifyOAuthMemberFailTest() {
        // given
        Long memberId = 1L;
        String nickname = "modifyNickname";
        String email = "modifyEmail";
        String number = "01064613134";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> memberService.modifyOAuthMember(memberId, nickname, email, number))
                .isInstanceOf(CustomException.class);

        // then
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void modifyMember() {
        // given
        Long memberId = 1L;
        String username = "updateUsername";
        String password = "updatePassword";
        String nickname = "updateNickname";
        String email = "updateEmail";
        String number = "01012345678";

        Member mockMember = Member.builder().build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        memberService.modifyMember(memberId, username, password, nickname, email, number);

        // then
        verify(memberRepository).findById(memberId);
        assertThat(mockMember.getUsername()).isEqualTo(username);
        assertThat(mockMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    void modifyMemberFailTest() {
        // given
        Long memberId = 1L;
        String username = "updateUsername";
        String password = "updatePassword";
        String nickname = "updateNickname";
        String email = "updateEmail";
        String number = "01012345678";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> memberService.modifyMember(memberId, username, password, nickname, email, number))
                .isInstanceOf(CustomException.class);

        // then
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void authenticateRegionTest() {
        // given
        Long memberId = 1L;
        RegionType regionType = RegionType.ULSAN;
        Member mockMember = Member.builder().region(RegionType.KYEONGNAM).build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        memberService.authenticateRegion(memberId, regionType);

        // then
        verify(memberRepository).findById(memberId);
        assertThat(mockMember.getRegion()).isEqualTo(regionType);
    }

    @Test
    void authenticateRegionFailTest() {
        // given
        Long memberId = 1L;
        RegionType regionType = RegionType.ULSAN;
        Member mockMember = Member.builder().region(RegionType.KYEONGNAM).build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> memberService.authenticateRegion(memberId, regionType))
                .isInstanceOf(CustomException.class);

        // then
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void findByIdTest() {
        // given
        Long memberId = 1L;
        Member mockMember = Member.builder().build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        Member findMember = memberService.findById(memberId);

        // then
        assertThat(findMember).isEqualTo(mockMember);
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void findByIdFailTest() {
        // given
        Long memberId = 1L;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> memberService.findById(memberId))
                .isInstanceOf(CustomException.class);

        // then
        verify(memberRepository, times(1)).findById(memberId);
    }
}
