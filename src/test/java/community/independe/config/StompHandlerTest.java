package community.independe.config;

import community.independe.config.handler.StompHandler;
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
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StompHandlerTest {

    @InjectMocks
    private StompHandler stompHandler;
    @Mock
    private JwtTokenVerifier jwtTokenVerifier;

    @Test
    void preSendTest() {
        // given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "mockToken");
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("payload", accessor.getMessageHeaders());

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // stub
        doNothing().when(jwtTokenVerifier).verifyToken(accessor.getFirstNativeHeader("Authorization"));

        // when
        stompHandler.preSend(message, mock(MessageChannel.class));

        // then
        verify(jwtTokenVerifier).verifyToken("mockToken");
    }
}
