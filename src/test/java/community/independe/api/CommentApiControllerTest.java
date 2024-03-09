package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.IntegrationTestSupporter;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LoginMemberInjector injector;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    public void setup() throws Exception {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        injector.makeAccessAndRefreshToken();
        accessToken = injector.getAccessToken();
        refreshToken = injector.getRefreshToken();
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    void createParentCommentTest() throws Exception {
        // given
        String content = "content";
        CreateParentCommentRequest createParentCommentRequest = new CreateParentCommentRequest();
        Member testUser = memberService.findByUsername("testUsername");
        Long savedIndependentPostId = postService.createIndependentPost(testUser.getId(), "title", "content", IndependentPostType.COOK);
        createParentCommentRequest.setContent(content);
        createParentCommentRequest.setPostId(savedIndependentPostId);

        // when
        ResultActions perform = mockMvc.perform(post("/api/comments/parent/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .content(objectMapper.writeValueAsString(createParentCommentRequest)));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void createChildComment() throws Exception {
        // given
        String content = "content";
        Member testUser = memberService.findByUsername("testUsername");
        Long savedIndependentPostId = postService.createIndependentPost(testUser.getId(), "title", "content", IndependentPostType.COOK);
        Long savedParentCommentId = commentService.createParentComment(testUser.getId(), savedIndependentPostId, content);
        CreateChildCommentRequest createChildCommentRequest = new CreateChildCommentRequest();
        createChildCommentRequest.setPostId(savedIndependentPostId);
        createChildCommentRequest.setParentId(savedParentCommentId);
        createChildCommentRequest.setContent(content);

        // when
        ResultActions perform = mockMvc.perform(post("/api/comments/child/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .content(objectMapper.writeValueAsString(createChildCommentRequest)));

        // then
        perform.andExpect(status().isOk());
    }
}
