package community.independe.repository;

import community.independe.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void initData() {
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);
    }

    @Test
    public void saveTest() {
        // given
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(()
                -> new IllegalArgumentException("Member not exist"));

        // then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void findByUsernameTest() {
        // given
        String username = "id";

        // when
        Member findMember = memberRepository.findByUsername(username);

        // then
        assertThat(findMember.getUsername()).isEqualTo(username);
    }
}