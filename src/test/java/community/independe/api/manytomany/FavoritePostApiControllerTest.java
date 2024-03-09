package community.independe.api.manytomany;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.IntegrationTestSupporter;
import community.independe.api.LoginMemberInjector;
import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import community.independe.service.manytomany.FavoritePostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoritePostApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FavoritePostRepository favoritePostRepository;
    @Autowired
    private FavoritePostService favoritePostService;
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
    void addFavoritePostTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);

        // when
        ResultActions perform = mockMvc.perform(post("/api/favoritePost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void addFavoritePostFalseTest() throws Exception {
        // when
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedFavoritePostId = favoritePostService.save(savedPostId, findMember.getId());

        // when
        ResultActions perform = mockMvc.perform(post("/api/favoritePost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        FavoritePost findFavoritePost = favoritePostRepository.findById(savedFavoritePostId).get();
        assertThat(findFavoritePost.getIsFavorite()).isFalse();
    }

    @Test
    void addFavoritePostTrueTest() throws Exception {
        // when
        Member findMember = memberService.findByUsername("testUsername");
        Long savedPostId = postService.createIndependentPost(findMember.getId(), "title", "content", IndependentPostType.CLEAN);
        Long savedFavoritePostId = favoritePostService.save(savedPostId, findMember.getId());
        mockMvc.perform(post("/api/favoritePost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // when
        ResultActions perform = mockMvc.perform(post("/api/favoritePost/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        FavoritePost findFavoritePost = favoritePostRepository.findById(savedFavoritePostId).get();
        assertThat(findFavoritePost.getIsFavorite()).isTrue();
    }
}
