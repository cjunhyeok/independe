package community.independe.config;

import community.independe.config.handler.StompHandler;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.chat.ChatSessionService;
import community.independe.util.JwtTokenVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StompHandlerTest {

    @InjectMocks
    private StompHandler stompHandler;
    @Mock
    private JwtTokenVerifier jwtTokenVerifier;
    @Mock
    private ChatSessionService chatSessionService;

    @Test
    void preSendConnectTest() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "mockToken");
        String sessionId = "sessionId";
        accessor.setSessionId(sessionId);
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("payload", accessor.getMessageHeaders());

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Member member = Member.builder().username("username").password("password").build();
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        MemberContext memberContext = new MemberContext(member, roles);

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(accessor.getFirstNativeHeader("Authorization"));
        when(authentication.getPrincipal()).thenReturn(memberContext);
        doNothing().when(chatSessionService).enterSocketSession(sessionId, null);

        // when
        stompHandler.preSend(message, mock(MessageChannel.class));

        // then
        verify(jwtTokenVerifier).verifyToken("mockToken");
    }

    @Test
    void preSendPrivateSubscribeTest() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setNativeHeader("Authorization", "mockToken");
        accessor.setDestination("/user/1/private");
        String sessionId = "sessionId";
        accessor.setSessionId(sessionId);
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("payload", accessor.getMessageHeaders());

        Member member = Member.builder().username("username").password("password").build();

        // stub
        when(chatSessionService.getMemberSocketSession(sessionId)).thenReturn(member);
        doNothing().when(chatSessionService).enterChatRoom(null, 1L);

        // when
        stompHandler.preSend(message, mock(MessageChannel.class));

        // then
        verify(chatSessionService, times(1)).getMemberSocketSession(sessionId);
        verify(chatSessionService, times(1)).enterChatRoom(null, 1L);
    }

    @Test
    void preSendRoomSubscribeTest() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setNativeHeader("Authorization", "mockToken");
        accessor.setDestination("/user/1/room");
        String sessionId = "sessionId";
        accessor.setSessionId(sessionId);
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("payload", accessor.getMessageHeaders());

        // when
        stompHandler.preSend(message, mock(MessageChannel.class));

        // then
        verifyNoInteractions(chatSessionService);
    }

    @Test
    void preSendUnSubscribeTest() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
        accessor.setNativeHeader("Authorization", "mockToken");
        accessor.setNativeHeader("Destination", "1");
        String sessionId = "sessionId";
        accessor.setSessionId(sessionId);
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("payload", accessor.getMessageHeaders());

        Member member = Member.builder().username("username").password("password").build();

        // stub
        when(chatSessionService.getMemberSocketSession(sessionId)).thenReturn(member);
        doNothing().when(chatSessionService).leaveChatRoom(null, 1L);

        // when
        stompHandler.preSend(message, mock(MessageChannel.class));

        // then
        verify(chatSessionService, times(1)).getMemberSocketSession(sessionId);
        verify(chatSessionService, times(1)).leaveChatRoom(null, 1L);
    }

    @Test
    void preSendDisconnectTest() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        accessor.setNativeHeader("Authorization", "mockToken");
        String sessionId = "sessionId";
        accessor.setSessionId(sessionId);
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("payload", accessor.getMessageHeaders());

        // stub
        doNothing().when(chatSessionService).removeSocketSession(sessionId);

        // when
        stompHandler.preSend(message, mock(MessageChannel.class));

        // then
        verify(chatSessionService, times(1)).removeSocketSession(sessionId);
    }
}
