package community.independe.api;

import community.independe.api.dtos.chat.Message;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.chat.ChatService;
import community.independe.util.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate; // 특정 상대에게 메시지를 보내기 위한 객체
    private final JwtTokenVerifier jwtTokenVerifier;

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message, @Header(name = "Authorization") String header){

        if (!jwtTokenVerifier.verifyToken(header)) {
            throw new RuntimeException();
        }
        Member loginMember = ((MemberContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMember();

        message.setSenderNickname(loginMember.getNickname());
        message.setCreatedDate(LocalDateTime.now());

        simpMessagingTemplate.convertAndSendToUser(message.getChatRoomId().toString(),"/private",message);
        Long savedChat = chatService.saveChat(loginMember.getId(), message.getReceiverId(), message.getMessage(), false);

        return message;
    }
}
