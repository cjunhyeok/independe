package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.CommentService;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import org.assertj.core.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private CommentService commentService;
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

    @Test
    void regionPostsTest() throws Exception {

        // given
        Member testUser = memberService.findByUsername("username");
        Long savedRegionPostId = postService.createRegionPost(
                testUser.getId(),
                "regionTitle",
                "regionContent",
                RegionType.ALL,
                RegionPostType.FREE);

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/region/ALL/FREE"));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].postId").value(savedRegionPostId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].nickName").value("testNick"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("regionTitle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].views").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].commentCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].picture").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1));
    }

    @Test
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void createRegionPostTest() throws Exception {
        // given
        String title = "regionTitle";
        String content = "regionContent";
        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;

        // when
        ResultActions perform = mockMvc.perform(post("/api/posts/region/new")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", title)
                .param("content", content)
                .param("regionType", regionType.name())
                .param("regionPostType", regionPostType.name()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updatePostTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPostId = postService.createIndependentPost(
                testUser.getId(),
                "title",
                "content",
                IndependentPostType.COOK);

        String updateTitle = "updateTitle";
        String updateContent = "updateContent";

        // when
        ResultActions perform = mockMvc.perform(put("/api/posts/" + savedIndependentPostId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", updateTitle)
                .param("content", updateContent));
        Post updatePost = postService.findById(savedIndependentPostId);

        // then
        perform.andExpect(status().isOk());
        Assertions.assertThat(updatePost.getTitle()).isEqualTo(updateTitle);
        Assertions.assertThat(updatePost.getContent()).isEqualTo(updateContent);
    }

    @Test
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deletePostTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPostId = postService.createIndependentPost(
                testUser.getId(),
                "title",
                "content",
                IndependentPostType.COOK);

        // when
        ResultActions perform = mockMvc.perform(delete("/api/posts/" + savedIndependentPostId));

        // then
        perform.andExpect(status().isOk());
        Post post = postService.findById(savedIndependentPostId);
//        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
//                () -> postService.findById(savedIndependentPostId));
    }

    @Test
    void postTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPostId = postService.createIndependentPost(
                testUser.getId(),
                "title",
                "content",
                IndependentPostType.COOK);
        Long savedCommentId = commentService.createParentComment(testUser.getId(), savedIndependentPostId, "comment");

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/" + savedIndependentPostId));

        // then
        perform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value("content"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testNick"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.independentPostType").value(IndependentPostType.COOK.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regionType").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regionPostType").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.independentPostTypeEn").value(IndependentPostType.COOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regionTypeEn").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.regionPostTypeEn").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.views").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentCount").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isRecommend").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isFavorite").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isReport").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.bestComment").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].commentId").value(savedCommentId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].nickname").value("testNick"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].content").value("comment"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].parentId").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].writerId").value(testUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].isRecommend").value(false));
    }

    @Test
    void searchPostTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("username");
        Long savedIndependentPostId = postService.createIndependentPost(
                testUser.getId(),
                "title",
                "content",
                IndependentPostType.COOK);
        String condition = "title";
        String keyword = "title";

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/search")
                .param("condition", condition)
                .param("keyword", keyword));

        // then
        perform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].postId").value(savedIndependentPostId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].nickname").value("testNick"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].independentPostType").value(IndependentPostType.COOK.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].regionType").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].regionPostType").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].independentPostTypeEn").value(IndependentPostType.COOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].regionTypeEn").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].regionPostTypeEn").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].views").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].commentCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].picture").value(false));
    }
    // todo
}