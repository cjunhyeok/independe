package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setup(@Autowired MemberRepository memberRepository) {
        Member member = Member.builder()
                .username("testUsername")
                .password("testPasswrod1!")
                .nickname("testNickname")
                .role("ROLE_USER")
                .build();

        memberRepository.save(member);
    }
}
