package community.independe.api;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.member.CreateMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@Transactional
public class MemberApiControllerTest extends IntegrationTestSupporter {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

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
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
