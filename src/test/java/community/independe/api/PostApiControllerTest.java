package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.IntegrationTestSupporter;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import community.independe.service.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FilesService filesService;
    @Autowired
    private VideoRepository videoRepository;
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
    void independentPostsTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("testUsername");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].nickName").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].title").value("testTitle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].views").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].commentCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].picture").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1));
    }

    @Test
    void independentPostKeywordTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("testUsername");
        Long savedIndependentPost = postService.createIndependentPost(testUser.getId(),
                "testTitle",
                "testContent",
                IndependentPostType.CLEAN);
        Video video = Video.builder()
                .videoTitle("만약 집에 씽크대가 있으면 꼭 봐야 할 영상!(놀라운 효과 증명) [싱크대/배수구/청소/냄새/악취/제거]")
                .videoUrl("https://www.youtube.com/embed/2p96FCJHjUM")
                .materName("살림톡")
                .independentPostType(IndependentPostType.CLEAN)
                .views(19000)
                .build();
        videoRepository.save(video);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        filesService.saveFiles(multipartFiles, savedIndependentPost);


        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/independent/CLEAN")
                .param("keyword", "test"));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].postId").value(savedIndependentPost))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].nickName").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].title").value("testTitle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].views").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].commentCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postsResponses[0].picture").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1));
    }

    @Test
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
                .header("Authorization", accessToken)
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void regionPostsTest() throws Exception {

        // given
        Member testUser = memberService.findByUsername("testUsername");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].nickName").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("regionTitle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].views").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].commentCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].picture").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(1));
    }

    @Test
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
                .param("regionPostType", regionPostType.name())
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void updatePostTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("testUsername");
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
                .param("content", updateContent)
                .header("Authorization", accessToken));
        Post updatePost = postService.findById(savedIndependentPostId);

        // then
        perform.andExpect(status().isOk());
        Assertions.assertThat(updatePost.getTitle()).isEqualTo(updateTitle);
        Assertions.assertThat(updatePost.getContent()).isEqualTo(updateContent);
    }

    @Test
    void deletePostTest() throws Exception {
        // todo
        // given
        Member testUser = memberService.findByUsername("testUsername");
        Long savedIndependentPostId = postService.createIndependentPost(
                testUser.getId(),
                "title",
                "content",
                IndependentPostType.COOK);

        // when
        ResultActions perform = mockMvc.perform(delete("/api/posts/" + savedIndependentPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
        Post post = postService.findById(savedIndependentPostId);
    }

    @Test
    void postTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("testUsername");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testNickname"))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].nickname").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].content").value("comment"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].recommendCount").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].parentId").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].writerId").value(testUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments[0].isRecommend").value(false));
    }

    @Test
    void searchPostTest() throws Exception {
        // given
        Member testUser = memberService.findByUsername("testUsername");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].nickname").value("testNickname"))
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
