package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.member.*;
import community.independe.service.MemberService;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;

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
    private PlatformTransactionManager transactionManager;

    private TransactionStatus transactionStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        memberService.join("testUsername", "testPasswrod1!", "testNickname", null, null);
        memberService.join("modifyMember", "testPasswrod1!", "modifyNickname", null, null);
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    @WithUserDetails(value = "testUsername", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void authenticateRegionTest() throws Exception {

        AuthenticationRegionRequest request = new AuthenticationRegionRequest();
        request.setRegion("경남");

        // 실행 및 검증
        mockMvc.perform(post("/api/members/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success Region Authentication"));
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
    @WithUserDetails(value = "testUsername", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void membersTest() throws Exception {

        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nickname").value("testNickname"))
                .andExpect(jsonPath("$[1].nickname").value("modifyNickname"));
    }

    @Test
    @WithUserDetails(value = "modifyMember", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
                .with(csrf()));

        // then
        perform.andExpect(status().isOk());
    }


    @Test
    @WithUserDetails(value = "modifyMember", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
}
