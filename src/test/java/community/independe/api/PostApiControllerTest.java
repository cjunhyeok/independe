package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        memberService.join("username", "testPass1!", "testNick", null, null);
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    void independentPostsTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPost = postService.createIndependentPost(testUser.getId(),
                "testTitle",
                "testContent",
                IndependentPostType.CLEAN);

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/independent/CLEAN"));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].postId").value(savedIndependentPost))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].nickName").value("testNick"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].title").value("testTitle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].views").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].commentCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].picture").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1));
    }

    @Test
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
