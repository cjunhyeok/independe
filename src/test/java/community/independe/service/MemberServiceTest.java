package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void joinTest() {

        // given
        String username = "id";
        String password = "1234";
        String nickname = "nick";

        // stub
        when(memberRepository.findByUsername(username)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            setPrivateField(member, "id", 1L);  // Reflection으로 ID 값을 설정
            return member;
        });

        // when
        Long joinMemberId = memberService.join(username, password, nickname, null, null);

        System.out.println(joinMemberId);

        verify(memberRepository).findByUsername(username);
        verify(passwordEncoder).encode(password);
        verify(memberRepository).save(any(Member.class));
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
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
}
