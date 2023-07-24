package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.comment.CreateChildCommentRequest;
import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.service.CommentService;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CommentService commentService;
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
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createParentCommentTest() throws Exception {
        // given
        String content = "content";
        CreateParentCommentRequest createParentCommentRequest = new CreateParentCommentRequest();
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPostId = postService.createIndependentPost(testUser.getId(), "title", "content", IndependentPostType.COOK);
        createParentCommentRequest.setContent(content);
        createParentCommentRequest.setPostId(savedIndependentPostId);

        // when
        ResultActions perform = mockMvc.perform(post("/api/comments/parent/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createParentCommentRequest)));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createChildComment() throws Exception {
        // given
        String content = "content";
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPostId = postService.createIndependentPost(testUser.getId(), "title", "content", IndependentPostType.COOK);
        Long savedParentCommentId = commentService.createParentComment(testUser.getId(), savedIndependentPostId, content);
        CreateChildCommentRequest createChildCommentRequest = new CreateChildCommentRequest();
        createChildCommentRequest.setPostId(savedIndependentPostId);
        createChildCommentRequest.setParentId(savedParentCommentId);
        createChildCommentRequest.setContent(content);

        // when
        ResultActions perform = mockMvc.perform(post("/api/comments/child/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createChildCommentRequest)));

        // then
        perform.andExpect(status().isOk());
    }
}
