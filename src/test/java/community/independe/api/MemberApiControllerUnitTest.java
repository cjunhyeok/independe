package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberApiControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    private ObjectMapper objectMapper = new ObjectMapper();



    @Test
    @WithMockUser(username = "testUsername")
    void createMemberTest() throws Exception {

        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setUsername("testUsername");
        createMemberRequest.setPassword("Aasdf123!@");
        createMemberRequest.setNickname("nick12");

        ResultActions perform = mockMvc.perform(post("/api/members/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest))
                .with(csrf()));

        perform.andExpect(status().isOk());
    }
}