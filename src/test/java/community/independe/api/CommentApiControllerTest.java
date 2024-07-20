package community.independe.api;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.comment.CreateChildCommentRequest;
import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.api.dtos.member.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
public class CommentApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String COMMONPASSWORD = "Password12!@";

    @Test
    @DisplayName("부모 댓글을 작성한다.")
    void createParentComment() throws Exception {
        // given
        initMemberSave();
        String token = getAccessToken("username");
        Long postId = initPostSave(token);
        CreateParentCommentRequest request = new CreateParentCommentRequest();
        request.setPostId(postId);
        request.setContent("content");

        // when
        ResultActions perform = mockMvc.perform(post("/api/comments/parent/new")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("자식 댓글을 작성한다.")
    void createChildComment() throws Exception {
        // given
        initMemberSave();
        String token = getAccessToken("username");
        Long postId = initPostSave(token);
        CreateParentCommentRequest parentRequest = new CreateParentCommentRequest();
        parentRequest.setPostId(postId);
        parentRequest.setContent("content");
        ResultActions perform = mockMvc.perform(post("/api/comments/parent/new")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(parentRequest))
                .contentType(MediaType.APPLICATION_JSON));
        String returnValue = perform.andReturn().getResponse().getContentAsString();
        Long commentId = Long.valueOf(returnValue);
        CreateChildCommentRequest request = new CreateChildCommentRequest();
        request.setParentId(commentId);
        request.setPostId(postId);
        request.setContent("content");

        // when
        perform = mockMvc.perform(post("/api/comments/child/new")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void initMemberSave() throws Exception {
        CreateMemberRequest request = CreateMemberRequest
                .builder()
                .isTermOfUseCheck(true)
                .isPrivacyCheck(true)
                .username("username")
                .password(COMMONPASSWORD)
                .nickname("nickname")
                .email("email@example.com")
                .number("010-1234-5678")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/members/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));
    }

    private String getAccessToken(String username) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(COMMONPASSWORD);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        return perform.andReturn().getResponse().getHeader("Authorization");
    }

    private Long initPostSave(String token) throws Exception {
        ResultActions perform = mockMvc.perform(multipart("/api/posts/independent/new")
                .header("Authorization", token)
                .param("title", "title")
                .param("content", "content")
                .param("independentPostType", "ETC")
                .contentType(MediaType.MULTIPART_FORM_DATA));

        String returnValue = perform.andReturn().getResponse().getContentAsString();
        return Long.valueOf(returnValue);
    }
}
