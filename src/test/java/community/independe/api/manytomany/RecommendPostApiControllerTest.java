package community.independe.api.manytomany;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.LoginMemberInjector;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import community.independe.service.manytomany.RecommendPostService;
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

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendPostApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecommendPostService recommendPostService;
    @Autowired
    private RecommendPostRepository recommendPostRepository;
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
    void addRecommendPostTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);

        // when
        ResultActions perform = mockMvc.perform(post("/api/recommendPost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void addRecommendPostFalseTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedRecommendPostId = recommendPostService.save(savedPostId, findMember.getId());

        // when
        ResultActions perform = mockMvc.perform(post("/api/recommendPost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        RecommendPost findRecommendPost = recommendPostRepository.findById(savedRecommendPostId).get();
        assertThat(findRecommendPost.getIsRecommend()).isFalse();
    }

    @Test
    void addRecommendPostTrueTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedRecommendPostId = recommendPostService.save(savedPostId, findMember.getId());
        mockMvc.perform(post("/api/recommendPost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // when
        ResultActions perform = mockMvc.perform(post("/api/recommendPost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        RecommendPost findRecommendPost = recommendPostRepository.findById(savedRecommendPostId).get();
        assertThat(findRecommendPost.getIsRecommend()).isTrue();
    }
}
