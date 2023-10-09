package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.member.*;
import community.independe.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
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

        memberService.join("modifyMember", "testPasswrod1!", "modifyNickname", null, null);

        injector.makeAccessAndRefreshToken();
        accessToken = injector.getAccessToken();
        refreshToken = injector.getRefreshToken();
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    void authenticateRegionTest() throws Exception {
        AuthenticationRegionRequest request = new AuthenticationRegionRequest();
        request.setRegion("경남");

        // 실행 및 검증
        mockMvc.perform(post("/api/members/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Success Region Authentication"));
    }

    @Test
    void authenticationBranchTest() throws Exception {
        // given
        AuthenticationRegionRequest request = new AuthenticationRegionRequest();
        String[] regions = new String[]{"서울", "울산", "부산", "경남", "대전"};

        // when
        for (String region : regions) {
            request.setRegion(region);

            ResultActions perform = mockMvc.perform(post("/api/members/region")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", accessToken));

            // then
            if (region.equals("대전")) {
                perform.andExpect(status().isBadRequest());
            } else {
                perform.andExpect(status().isOk());
            }
        }
    }

    @Test
    void createMemberTest() throws Exception {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setUsername("username");
        createMemberRequest.setPassword("Aasdf123!@");
        createMemberRequest.setNickname("nick12");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest))
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void createMemberFailTest() throws Exception {
        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setUsername("testUsername");
        createMemberRequest.setPassword("abc");
        createMemberRequest.setNickname("nick12");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest))
                .with(csrf()));

        // then
        perform.andExpect(status().isBadRequest());
    }

    @Test
    void duplicateUsernameTest() throws Exception {
        // given
        DuplicateUsernameRequest duplicateUsernameRequest = new DuplicateUsernameRequest();
        duplicateUsernameRequest.setUsername("username");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUsernameRequest))
                .with(csrf()));

        // then
        perform.andExpect(jsonPath("$.idDuplicatedNot").value(true));
    }

    @Test
    void duplicateUsernameFailTest() throws Exception {
        // given
        DuplicateUsernameRequest duplicateUsernameRequest = new DuplicateUsernameRequest();
        duplicateUsernameRequest.setUsername("testUsername");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUsernameRequest))
                .with(csrf()));

        // then
        perform.andExpect(jsonPath("$.idDuplicatedNot").value(false));
    }

    @Test
    void duplicateNicknameTest() throws Exception {
        // given
        DuplicateNicknameRequest duplicateNicknameRequest = new DuplicateNicknameRequest();
        duplicateNicknameRequest.setNickname("nickname");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateNicknameRequest))
                .with(csrf()));

        // then
        perform.andExpect(jsonPath("$.idDuplicatedNot").value(true));
    }

    @Test
    void duplicateNicknameFailTest() throws Exception {
        // given
        DuplicateNicknameRequest duplicateNicknameRequest = new DuplicateNicknameRequest();
        duplicateNicknameRequest.setNickname("testNickname");

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateNicknameRequest))
                .with(csrf()));

        // then
        perform.andExpect(jsonPath("$.idDuplicatedNot").value(false));
    }

    @Test
    void membersTest() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .with(csrf()));

        // then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void modifyOAuthMembersTest() throws Exception {
        // given
        OAuthMemberRequest oAuthMemberRequest = new OAuthMemberRequest();
        oAuthMemberRequest.setNickname("OAuthNick");
        oAuthMemberRequest.setNumber("01016161334");
        oAuthMemberRequest.setEmail("testEmail");

        // when
        ResultActions perform = mockMvc.perform(put("/api/oauth/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oAuthMemberRequest))
                .header("Authorization", accessToken)
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }


    @Test
    void modifyMembersTest() throws Exception {
        // given
        ModifyMemberRequest modifyMemberRequest = new ModifyMemberRequest();
        modifyMemberRequest.setUsername("modifyUsername");
        modifyMemberRequest.setPassword("modifyPW12!");
        modifyMemberRequest.setNickname("modifyNickname");

        // when
        ResultActions perform = mockMvc.perform(put("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyMemberRequest))
                .header("Authorization", accessToken)
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void loginTest() throws Exception {
        // given
        String username = "testUsername";
        String password = "testPasswrod1!";

        // when
        ResultActions perform = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "password", password
                )))
                .with(csrf()));

        // then
         perform.andExpect(status().isOk());
    }

    @Test
    void loginUsernameFailTest() throws Exception {
        // given
        String username = "testUsernameFail";
        String password = "testPasswrod1!";

        // when
        ResultActions perform = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "password", password
                )))
                .with(csrf()));

        perform.andExpect(status().isUnauthorized());
    }

    @Test
    void loginPasswordFailTest() throws Exception {
        // given
        String username = "testUsername";
        String password = "testPasswrod1!Fail";

        // when
        ResultActions perform = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "password", password
                )))
                .with(csrf()));

        perform.andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenTest() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(post("/api/refreshToken")
                .header("RefreshToken", "Bearer " + refreshToken));
        Cookie cookie = perform.andReturn().getResponse().getCookie("refreshToken");
        String header = perform.andReturn().getResponse().getHeader("Authorization");

        // then
        perform.andExpect(status().isOk());
        assertThat(cookie.getValue()).isNotEmpty();
        assertThat(header).isNotEmpty();
    }

    @Test
    void refreshTokenFindFailTest() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(post("/api/refreshToken"));

        // then
        perform.andExpect(status().isBadRequest());
    }
}
