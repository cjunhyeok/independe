package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.member.AuthenticationRegionRequest;
import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.api.dtos.member.DuplicateUsernameRequest;
import community.independe.domain.member.Member;
import community.independe.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberApiControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        UserDetails userDetails = User.withUsername("username")
                .password("password12")
                .roles("USER")
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    @WithMockUser(username = "testUsername")
    void createMemberTest() throws Exception {

        // given
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setUsername("testUsername");
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
    @WithMockUser(username = "testUsername")
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
    @WithMockUser(username = "testUsername")
    void duplicateUsernameTest() throws Exception {

        // given
        DuplicateUsernameRequest duplicateUsernameRequest = new DuplicateUsernameRequest();
        duplicateUsernameRequest.setUsername("testUsername");
        Member mockMember = Member.builder().build();

        // stub
        when(memberService.findByUsername(duplicateUsernameRequest.getUsername())).thenReturn(mockMember);

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUsernameRequest))
                .with(csrf()));

        // then
        perform.andExpect(jsonPath("$.idDuplicatedNot").value(false));
    }

    @Test
    @WithMockUser(username = "testUsername")
    void duplicateUsernameFailTest() throws Exception {

        // given
        DuplicateUsernameRequest duplicateUsernameRequest = new DuplicateUsernameRequest();
        duplicateUsernameRequest.setUsername("testUsername");

        // stub
        when(memberService.findByUsername(duplicateUsernameRequest.getUsername())).thenReturn(null);

        // when
        ResultActions perform = mockMvc.perform(post("/api/members/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUsernameRequest))
                .with(csrf()));

        // then
        perform.andExpect(jsonPath("$.idDuplicatedNot").value(true));
    }


    @Test
    @WithUserDetails(value = "username", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
}