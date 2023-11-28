package community.independe.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.api.dtos.chat.Message;
import community.independe.config.handler.StompHandler;
import community.independe.domain.member.Member;
import community.independe.service.MemberService;
import community.independe.service.chat.ChatRoomService;
import community.independe.util.JwtTokenVerifier;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatApiControllerTest {

    @LocalServerPort
    private Integer port;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private StompHandler stompHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;
    @Autowired private AbstractSubscribableChannel clientInboundChannel;

    @Autowired private AbstractSubscribableChannel clientOutboundChannel;

    @Autowired private AbstractSubscribableChannel brokerChannel;
    @Autowired private JwtTokenVerifier jwtTokenVerifier;
    private ChannelInterceptor brokerChannelInterceptor = new StompHandler(jwtTokenVerifier);
    private final BlockingQueue<org.springframework.messaging.Message<?>> messages = new ArrayBlockingQueue<>(100);

    private static final String WEBSOCKET_TOPIC = "/user/private";
    private static final String CHAT_ENDPOINT = "/pub/private-message";

    @Test
    void receivePrivateMessageTest() throws Exception {

        // 초기 데이터 및 로그인 user 설정
        memberService.join("testUsername", "testPasswrod1!", "testNickname", null, null);
        String username = "testUsername";
        String password = "testPasswrod1!";
        ResultActions perform = mockMvc.perform(post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "username", username,
                        "password", password
                )))
                .with(csrf()));
        Cookie refreshTokenCookie = perform.andReturn().getResponse().getCookie("refreshToken");
        accessToken = perform.andReturn().getResponse().getHeader("Authorization");
        Member findMember = memberService.findByUsername("testUsername");
        Long savedReceiverId = memberService.join("receiver", "pass1", "receiver", "email", "number");
        Long savedChatRoomId = chatRoomService.saveChatRoom(findMember.getId(), savedReceiverId);

        // stomp 설정
        CompletableFuture<Message> completableFuture = new CompletableFuture<>();
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                        List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", accessToken);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", accessToken);

        StompSessionHandler sessionHandler = new TestSessionHandler(completableFuture);

        String wsPath = getWsPath();
        CompletableFuture<StompSession> connect = stompClient.connectAsync(wsPath, webSocketHttpHeaders, stompHeaders, sessionHandler);
        StompSession stompSession = connect.get(1, TimeUnit.SECONDS);

        Message message = createMessage(savedChatRoomId, savedReceiverId, findMember.getNickname());

//        stompHeaders.setDestination(getSubPath(savedChatRoomId));
        stompSession.subscribe(getSubPath(savedChatRoomId), new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                System.out.println("payload : " + ((Message) payload).getMessage());
                completableFuture.complete((Message) payload);
            }
        });
//        stompSession.subscribe(getSubPath(savedChatRoomId), new StompFrameHandler() {
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return Message.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//
//                System.out.println("payload : " + ((Message) payload).getMessage());
//                completableFuture.complete((Message) payload);
//            }
//        });

        stompHeaders.setDestination(getSendPath());
        stompSession.send(stompHeaders, message);

//        await()
//                .atMost(10, SECONDS)
//                .untilAsserted(() -> Assertions.assertThat(blockingQueue.poll()).isNotEmpty());

//        Message receivedMessage = completableFuture.get(10, TimeUnit.SECONDS);
//
//        Assertions.assertThat(receivedMessage).isNotNull();
    }

    private class TestSessionHandler extends StompSessionHandlerAdapter {

        private final CompletableFuture<Message> completableFuture;

        public TestSessionHandler(CompletableFuture<Message> completableFuture) {
            this.completableFuture = completableFuture;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete((Message) payload);
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }
    }

    private Message createMessage(Long chatRoomId, Long receiverId, String senderNickname) {
        Message message = new Message();
        message.setMessage("message");
        message.setChatRoomId(chatRoomId);
        message.setReceiverId(receiverId);
        message.setSenderNickname(senderNickname);

        return message;
    }

    private String getWsPath() {
        return String.format("ws://localhost:%d/ws", port);
    }
    private String getSubPath(Long chatRoomId) {
        return String.format("/user/%d/private", chatRoomId);
    }
    private String getSendPath() {
        return String.format("/pub/private-message");
    }
}
