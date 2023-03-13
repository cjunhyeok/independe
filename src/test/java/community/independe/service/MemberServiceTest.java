package community.independe.service;

import community.independe.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback(value = false)
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void joinTest() {

        // given
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        // when
        Long joinMemberId = memberService.join("id1", "1234", "nickname", null, null, null, null, null);
        Member findMember = memberService.findById(joinMemberId);

        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

    }
}
