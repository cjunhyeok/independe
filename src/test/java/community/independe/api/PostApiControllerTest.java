package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    @WithUserDetails(value = "testUsername", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createIndependentPostTest() throws Exception {
        // given
        String title = "testTitle";
        String content = "testContent";
        IndependentPostType independentPostType = IndependentPostType.CLEAN;

        // when
        ResultActions perform = mockMvc.perform(post("/api/posts/independent/new")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", title)
                .param("content", content)
                .param("independentPostType", independentPostType.name())
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }


}
