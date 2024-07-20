package community.independe.api;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.member.*;
import org.assertj.core.api.Assertions;
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

@Transactional
public class MemberApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String COMMONPASSWORD = "Password12!@";

    @Test
    @DisplayName("회원가입을 한다.")
    void createMemberTest() throws Exception {
        // given
        CreateMemberRequest request = CreateMemberRequest
                .builder()
                .isTermOfUseCheck(true)
                .isPrivacyCheck(true)
                .username("username")
                .password("Password12!@")
                .nickname("nickname")
                .email("email@example.com")
                .number("010-1234-5678")
                .build();

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/members/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("아이디 중복을 확인한다.")
    void duplicateUsernameTest() throws Exception {
        // given
        String username = "username";
        DuplicateUsernameRequest request = new DuplicateUsernameRequest();
        request.setUsername(username);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/members/username")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("닉네임 중복을 확인한다.")
    void duplicateNicknameTest() throws Exception {
        // given
        String nickname = "nickname";
        DuplicateNicknameRequest request = new DuplicateNicknameRequest();
        request.setNickname(nickname);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/members/nickname")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("로그인을 진행한다.")
    void loginTest() throws Exception {
        // given
        String username = "username";
        String nickname = "nickname";
        initSave(username, nickname);
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(COMMONPASSWORD);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
        String authorization = perform.andReturn().getResponse().getHeader("Authorization");
        String refreshToken = perform.andReturn().getResponse().getCookie("refreshToken").getValue();
        Assertions.assertThat(authorization).startsWith("Bearer ");
        Assertions.assertThat(refreshToken).contains(".");
    }

    @Test
    @DisplayName("서울 위치인증을 진행한다.")
    void authenticateRegionTest() throws Exception {
        // given
        String username = "username";
        String region = "서울";
        initSave(username, "nickname");
        String accessToken = getAccessToken(username);
        AuthenticationRegionRequest request = new AuthenticationRegionRequest();
        request.setRegion(region);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/members/region")
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    void modifyMembersTest() throws Exception {
        // given
        String username = "username";
        String nickname = "modifyNickname";
        String email = "modifyEmail@example.com";
        String number = "010-5678-1234";
        initSave(username, "nickname");
        String accessToken = getAccessToken(username);
        ModifyMemberRequest request = ModifyMemberRequest
                .builder()
                .nickname(nickname)
                .email(email)
                .number(number)
                .build();

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/members")
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원 비밀번호를 수정한다.")
    void modifyMemberTest() throws Exception {
        // given
        String username = "username";
        String password = "ModifyPassword12!";
        initSave(username, "nickname");
        String accessToken = getAccessToken(username);
        ModifyPasswordRequest request = ModifyPasswordRequest
                .builder()
                .password(password)
                .build();

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/members/password")
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("리프레시 토큰이 재발급된다.")
    void refreshTokenTest() throws Exception {
        // given
        String username = "username";
        String nickname = "nickname";
        initSave(username, nickname);
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(COMMONPASSWORD);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));
        String refreshToken = perform.andReturn().getResponse().getCookie("refreshToken").getValue();

        // when
        perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/refreshToken")
                .content(objectMapper.writeValueAsString(request))
                .header("RefreshToken", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );


        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
        String authorization = perform.andReturn().getResponse().getHeader("Authorization");
        refreshToken = perform.andReturn().getResponse().getCookie("refreshToken").getValue();
        Assertions.assertThat(authorization).startsWith("Bearer ");
        Assertions.assertThat(refreshToken).contains(".");
    }

    @Test
    @DisplayName("마이페이지 회원 데이터를 조회한다.")
    void getMyPageTest() throws Exception {
        // given
        String username = "username";
        initSave(username, "nickname");
        String accessToken = getAccessToken(username);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/member")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("작성한 글 목록을 조회한다.")
    void getMyPostTest() throws Exception {
        // given
        String username = "username";
        initSave(username, "nickname");
        String accessToken = getAccessToken(username);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/post")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("작성한 댓글 목록을 조회한다.")
    void getMyCommnetTest() throws Exception {
        // given
        String username = "username";
        initSave(username, "nickname");
        String accessToken = getAccessToken(username);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/member/comment")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void initSave(String username, String nickname) throws Exception {
        CreateMemberRequest request = CreateMemberRequest
                .builder()
                .isTermOfUseCheck(true)
                .isPrivacyCheck(true)
                .username(username)
                .password(COMMONPASSWORD)
                .nickname(nickname)
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
}
