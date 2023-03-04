package community.independe.domain;

import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testMember() {
        //given
        Member member = Member.builder()
                .username("member1")
                .password("123")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();

        //when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("user not exist"));

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @Test
    public void noMember() {
        //given
        Member member = Member.builder()
                .username("member1")
                .password("123")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> memberRepository.findById(2L)
                        .orElseThrow(() -> new IllegalArgumentException()));
    }
}
