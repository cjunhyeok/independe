package community.independe.api.manytomany;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.LoginMemberInjector;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.service.CommentService;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import community.independe.service.manytomany.RecommendCommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendCommentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecommendCommentService recommendCommentService;
    @Autowired
    private RecommendCommentRepository recommendCommentRepository;
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

    @BeforeEach
    public void setup() throws Exception {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        injector.makeAccessAndRefreshToken();
        accessToken = injector.getAccessToken();
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    void addRecommendCommentTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedCommentId = commentService.createParentComment(findMember.getId(), savedPostId, "content");

        // when
        ResultActions perform = mockMvc.perform(post("/api/recommendComment/{commentId}", savedCommentId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void addRecommendCommentFalseTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedCommentId = commentService.createParentComment(findMember.getId(), savedPostId, "content");
        Long savedRecommendCommentId = recommendCommentService.save(savedCommentId, findMember.getId());

        // when
        ResultActions perform = mockMvc.perform(post("/api/recommendComment/{commentId}", savedCommentId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        RecommendComment findRecommendComment = recommendCommentRepository.findById(savedRecommendCommentId).get();
        assertThat(findRecommendComment.getIsRecommend()).isFalse();
    }

    @Test
    void addRecommendCommentTrueTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedCommentId = commentService.createParentComment(findMember.getId(), savedPostId, "content");
        Long savedRecommendCommentId = recommendCommentService.save(savedCommentId, findMember.getId());
        mockMvc.perform(post("/api/recommendComment/{commentId}", savedCommentId)
                .header("Authorization", accessToken));

        // when
        ResultActions perform = mockMvc.perform(post("/api/recommendComment/{commentId}", savedCommentId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        RecommendComment findRecommendComment = recommendCommentRepository.findById(savedRecommendCommentId).get();
        assertThat(findRecommendComment.getIsRecommend()).isTrue();
    }
}
