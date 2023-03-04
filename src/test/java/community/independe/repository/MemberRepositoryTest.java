package community.independe.repository;

import community.independe.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void basicMemberTest() {

        Member member1 = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();

        Member member2 = Member.builder()
                .username("id2")
                .password("1234")
                .nickname("nick2")
                .role("ROLE_USER")
                .build();

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        Member findMember = memberRepository.findByUsername(savedMember1.getUsername());
        Assertions.assertThat(findMember).isEqualTo(savedMember1);

        List<Member> findMembers = memberRepository.findAll();
        Assertions.assertThat(findMembers.size()).isEqualTo(2);

        memberRepository.delete(savedMember2);
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(1);

    }
}