package community.independe.api;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.chat.ChatRoomRequest;
import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.api.dtos.member.LoginRequest;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@Transactional
public class ChatRoomApiControllerTest extends IntegrationTestSupporter {

    private static final String COMMONPASSWORD = "Password12!@";

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("받는 회원 PK 를 이용해 채팅방을 생성한다.")
    void chatRoomTest() throws Exception {
        // given
        String sender = "sender";
        String receiver = "receiver";
        initSave(sender, sender);
        Long receiverId = initSave(receiver, receiver);
        String accessToken = getAccessToken(sender);
        ChatRoomRequest request = ChatRoomRequest
                .builder()
                .receiverId(receiverId)
                .build();

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/chat/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", accessToken)
                .with(csrf()));

        // then
        String response = perform
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("채팅방 목록을 조회한다.")
    void chatRoomsTest() throws Exception {
        // given
        String user = "user";
        initSave(user, user);
        String accessToken = getAccessToken(user);

        // when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/chat/rooms")
                .header("Authorization", accessToken)
                .with(csrf()));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("채팅 내역을 조회한다.")
    void chatHistoryTest() throws Exception {
        String sender = "sender";
        String receiver = "receiver";
        initSave(sender, sender);
        Long receiverId = initSave(receiver, receiver);
        String accessToken = getAccessToken(sender);
        ChatRoomRequest request = ChatRoomRequest
                .builder()
                .receiverId(receiverId)
                .build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/chat/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", accessToken)
                .with(csrf()));
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String chatRoomId = jsonNode.path("data").path("chatRoomId").asText();

        // when
        perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/chat/history")
                .header("Authorization", accessToken)
                .param("chatRoomId", chatRoomId)
                .with(csrf()));

        // then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private Long initSave(String username, String nickname) throws Exception {
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

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/members/new")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        return Long.parseLong(perform.andReturn().getResponse().getContentAsString());
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
