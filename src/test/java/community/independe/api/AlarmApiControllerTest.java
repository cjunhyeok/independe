package community.independe.api;

import community.independe.IntegrationTestSupporter;
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

@Transactional
public class AlarmApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String COMMONPASSWORD = "Password12!@";

    @Test
    @DisplayName("내 알람을 조회한다.")
    void alarmListTest() throws Exception {
        // given
        initSave();
        String token = getAccessToken("username");

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/alarms")
                .header("Authorization", token));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void initSave() throws Exception {
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
}
