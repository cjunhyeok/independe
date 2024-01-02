package community.independe.config.handler;

import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.chat.ChatSessionService;
import community.independe.util.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ChatSessionService chatSessionService;


    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE + 99)
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = null;
        String sessionId = accessor.getSessionId();

        StompCommand command = accessor.getCommand();

        if (command == StompCommand.CONNECT) {
            token = accessor.getFirstNativeHeader("Authorization");
            jwtTokenVerifier.verifyToken(token);
            Member loginMember = ((MemberContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMember();

            chatSessionService.enterSocketSession(sessionId, loginMember.getId());
        } else if (command == StompCommand.SUBSCRIBE) {

            String destination = accessor.getDestination();
            Long chatRoomId = Long.parseLong(extractChatRoomId(destination));
            Member loginMember = chatSessionService.getMemberSocketSession(sessionId);

            // 세션(redis)에 회원 정보를 넣음
            chatSessionService.enterChatRoom(loginMember.getId(), chatRoomId);
        } else if (command == StompCommand.UNSUBSCRIBE) {
            // 세션(redis)에 회원 정보를 삭제
            Member loginMember = chatSessionService.getMemberSocketSession(sessionId);
            Long chatRoomId = Long.parseLong(accessor.getFirstNativeHeader("Destination"));
            chatSessionService.leaveChatRoom(loginMember.getId(), chatRoomId);
        } else if (command == StompCommand.DISCONNECT) {
            chatSessionService.removeSocketSession(sessionId);
        }
        return message;
    }

    private String extractChatRoomId(String destination) {
        int start = "/user/".length();
        int end = destination.indexOf("/private");
        return destination.substring(start, end);
    }
}

