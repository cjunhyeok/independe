package community.independe.api;

import community.independe.api.dtos.chat.Message;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.service.AlarmService;
import community.independe.service.EmitterService;
import community.independe.service.chat.ChatService;
import community.independe.service.chat.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatApiController {

    private final static String CHAT_MESSAGE = "채팅이 도착했습니다.";
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate; // 특정 상대에게 메시지를 보내기 위한 객체
    private final EmitterService emitterService;
    private final AlarmService alarmService;
    private final ChatSessionService chatSessionService;

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message, @Header("simpSessionId") String sessionId){

        Member loginMember = chatSessionService.getMemberSocketSession(sessionId);
        Set<String> chatRoomMembers = chatSessionService.getChatRoomMembers(message.getChatRoomId().toString());

        for (String chatRoomMember : chatRoomMembers) {
            if (!chatRoomMember.equals(loginMember.getId().toString())) {
                // 채팅방 세션에 나와 다른 사용자가 있으면
                // 읽음처리를 진행해야한다.
                message.setIsRead(true);
            }
        }

        message.setSenderNickname(loginMember.getNickname());
        message.setCreatedDate(LocalDateTime.now());

        Long savedChat = chatService.saveChat(message.getMessage(), loginMember.getId(), message.getReceiverId(), message.getChatRoomId(), message.getIsRead());
        message.setChatId(savedChat);
        simpMessagingTemplate.convertAndSendToUser(message.getChatRoomId().toString(),"/private",message);

        if (!message.getIsRead()) {
            emitterService.notify(message.getReceiverId(), CHAT_MESSAGE);
            alarmService.saveAlarm(CHAT_MESSAGE, message.getIsRead(), AlarmType.TALK, message.getReceiverId());
        }

        return message;
    }
}
