package community.independe.api;

import community.independe.domain.file.Files;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.service.FilesService;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import community.independe.service.dtos.JoinServiceDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilesApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FilesService filesService;
    @Autowired
    private LoginMemberInjector injector;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;
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
    void postFilesTest() throws Exception {
        // given
        JoinServiceDto joinServiceDto = createJoinServiceDto("mockUsername", "password", "mockNickname");
        Long joinMemberId = memberService.join(joinServiceDto);
        Long savedPostId = postService.createIndependentPost(joinMemberId, "title", "content", IndependentPostType.CLEAN);
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile eventImage1 = new MockMultipartFile("eventImages", "image1.jpg", "image/jpeg", new byte[0]);
        images.add(eventImage1);
        List<Files> files = filesService.saveFiles(images, savedPostId);

        // when
        ResultActions perform = mockMvc.perform(get("/api/files/{postId}", savedPostId)
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk());
    }

    private JoinServiceDto createJoinServiceDto(String username, String password, String nickname) {
        return JoinServiceDto.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .isPrivacyCheck(true)
                .isPrivacyCheck(true)
                .build();
    }
}
