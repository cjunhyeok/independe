package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.chat.ChatHistoryRequest;
import community.independe.api.dtos.chat.ChatRoomRequest;
import community.independe.api.dtos.chat.ChatRoomsResponse;
import community.independe.domain.member.Member;
import community.independe.service.MemberService;
import community.independe.service.chat.ChatRoomService;
import community.independe.service.chat.ChatService;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatRoomApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginMemberInjector injector;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    void chatRoomsTest() throws Exception {
        // given
        Member sender = memberService.findByUsername("testUsername");
        Long senderId = sender.getId();
        Long receiverId = memberService.join("receiver", "pass1", "receiver", null, null);
        Long secondReceiverId = memberService.join("secondReceiver", "pass1", "secondReceiver", null, null);
        Long chatRoomId = chatRoomService.saveChatRoom(senderId, receiverId);
        Long secondChatRoomId = chatRoomService.saveChatRoom(senderId, secondReceiverId);
        chatService.saveChat("message", senderId, receiverId, chatRoomId, false);
        chatService.saveChat("secondMessage", senderId, secondReceiverId, secondChatRoomId, false);

        List<ChatRoomsResponse> chatRooms = chatRoomService.findChatRooms(senderId);

        // when
        ResultActions perform = mockMvc.perform(get("/api/chat/rooms")
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2));
    }

    @Test
    void chatRoomTest() throws Exception {
        // given
        Long receiverId = memberService.join("receiver", "pass1", "receiver", null, null);
        ChatRoomRequest request = new ChatRoomRequest();
        request.setReceiverId(receiverId);

        // when
        ResultActions perform = mockMvc.perform(post("/api/chat/room")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        perform.andExpect(status().isOk());
    }

    @Test
    void chatRoomExistTest() throws Exception {
        // given
        Member sender = memberService.findByUsername("testUsername");
        Long receiverId = memberService.join("receiver", "pass1", "receiver", null, null);
        ChatRoomRequest request = new ChatRoomRequest();
        request.setReceiverId(receiverId);
        Long savedChatRoomId = chatRoomService.saveChatRoom(sender.getId(), receiverId);

        // when
        ResultActions perform = mockMvc.perform(post("/api/chat/room")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.chatRoomId").value(savedChatRoomId));
    }

    @Test
    void chatHistoryTest() throws Exception {
        // given
        ChatHistoryRequest request = new ChatHistoryRequest();

        Member sender = memberService.findByUsername("testUsername");
        Long senderId = sender.getId();
        Long receiverId = memberService.join("receiver", "pass1", "receiver", null, null);
        Long chatRoomId = chatRoomService.saveChatRoom(senderId, receiverId);
        chatService.saveChat("message", senderId, receiverId, chatRoomId, false);
        chatService.saveChat("secondMessage", senderId, receiverId, chatRoomId, false);

        request.setChatRoomId(chatRoomId);

        // when
        ResultActions perform = mockMvc.perform(get("/api/chat/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
