package community.independe.api;

import community.independe.api.dtos.chat.*;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.service.AlarmService;
import community.independe.service.EmitterService;
import community.independe.service.MemberService;
import community.independe.service.chat.ChatReadService;
import community.independe.service.chat.ChatRoomService;
import community.independe.service.chat.ChatService;
import community.independe.service.chat.ChatSessionService;
import community.independe.service.chat.dtos.SaveChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final ChatReadService chatReadService;

    @MessageMapping("/private-message")
    public ReceiveMessage receivePrivateMessage(@Payload SendMessage sendMessage, @Header("simpSessionId") String sessionId){

        Member loginMember = chatSessionService.getMemberSocketSession(sessionId);
        Set<String> chatRoomMembers = chatSessionService.getChatRoomMembers(sendMessage.getChatRoomId().toString());
        Member findSender = memberService.findById(sendMessage.getSenderId());

        // 채팅방 세션에 상대방이 있는지 확인 후 isRead 값을 가져온다.
        Boolean isRead = checkChatRoomParticipate(chatRoomMembers, loginMember);

        // 채팅을 저장하고 저장된 채팅 정보를 조회한다.
        Chat findChat = saveChat(sendMessage, findSender, isRead);

        // /user/{chatRoomId}/private 로 보낸다
        sendMessageToChatSocket(findChat, isRead, findSender, sendMessage.getChatRoomId());

        // /user/{username}/room 로 보낸다 (채팅방 내용)
        sendMessageToChatRoomSocket(sendMessage, loginMember);

        // isRead 가 false 이면 알람을 보낸다.
        sendAlarm(sendMessage, isRead);

        return makeReceiveMessage(findChat, isRead, findSender);
    }

    private Boolean checkChatRoomParticipate(Set<String> chatRoomMembers, Member loginMember) {
        Boolean isRead = false;
        for (String chatRoomMember : chatRoomMembers) {
            if (!chatRoomMember.equals(loginMember.getId().toString())) {
                // 채팅방 세션에 나와 다른 사용자가 있으면
                // 읽음처리를 진행해야한다.
                isRead = true;
            }
        }

        return isRead;
    }

    private Chat saveChat(SendMessage sendMessage, Member sender, Boolean isRead) {
        SaveChatDto saveChatDto = SaveChatDto
                .builder()
                .message(sendMessage.getMessage())
                .chatRoomId(sendMessage.getChatRoomId())
                .senderId(sender.getId())
                .receiverId(sendMessage.getReceiverId())
                .isRead(isRead)
                .build();
        Long savedChat = chatService.saveChat(saveChatDto);

        return chatService.findById(savedChat);
    }

    private void sendMessageToChatSocket(Chat findChat, Boolean isRead, Member sender, Long chatRoomId) {
        ReceiveMessage receiveMessage = ReceiveMessage.builder()
                .message(findChat.getMessage())
                .chatId(findChat.getId())
                .isRead(isRead)
                .senderId(sender.getId())
                .senderNickname(sender.getNickname())
                .createdDate(findChat.getCreatedDate())
                .build();

        simpMessagingTemplate.convertAndSendToUser(chatRoomId.toString(),"/private",receiveMessage);
    }

    private void sendMessageToChatRoomSocket(SendMessage sendMessage, Member loginMember) {
        Member findReceiver = memberService.findById(sendMessage.getReceiverId());
        Long unReadCount = chatReadService.findUnReadCount(sendMessage.getChatRoomId(), findReceiver.getId());

        ChatRoomsResponse chatRoomsResponse = ChatRoomsResponse.builder()
                .lastMessage(sendMessage.getMessage())
                .chatRoomId(sendMessage.getChatRoomId())
                .senderNickname(loginMember.getNickname())
                .unReadCount(unReadCount)
                .build();
        simpMessagingTemplate.convertAndSendToUser(findReceiver.getUsername(), "/room", chatRoomsResponse);
    }

    private ReceiveMessage makeReceiveMessage(Chat findChat, Boolean isRead, Member sender) {
        return ReceiveMessage.builder()
                .chatId(findChat.getId())
                .message(findChat.getMessage())
                .isRead(isRead)
                .senderId(sender.getId())
                .senderNickname(sender.getNickname())
                .createdDate(findChat.getCreatedDate())
                .build();
    }

    private void sendAlarm(SendMessage sendMessage, Boolean isRead) {
        if (!isRead) {
            emitterService.notify(sendMessage.getReceiverId(), CHAT_MESSAGE);
            alarmService.saveAlarm(CHAT_MESSAGE, isRead, AlarmType.TALK, sendMessage.getReceiverId());
        }
    }

    @MessageExceptionHandler(CustomException.class)
    public Message handleCustomException(CustomException ex, @Payload Message message, @Header("simpSessionId") String sessionId) {

        Member loginMember = chatSessionService.getMemberSocketSession(sessionId);
        ExceptionMessage exceptionMessage = ExceptionMessage.builder()
                .exceptionMessage(ex.getMessage())
                .chatId(message.getChatId())
                .chatRoomId(message.getChatRoomId())
                .build();

        simpMessagingTemplate.convertAndSendToUser(loginMember.getUsername(), "/room", exceptionMessage);
        return message;
    }

    @MessageExceptionHandler(Exception.class)
    public Message exceptionHandler(Exception ex, @Payload Message message, @Header("simpSessionId") String sessionId) {

        Member loginMember = chatSessionService.getMemberSocketSession(sessionId);
        ExceptionMessage exceptionMessage = ExceptionMessage.builder()
                .exceptionMessage(ex.getMessage())
                .chatId(message.getChatId())
                .chatRoomId(message.getChatRoomId())
                .build();

        simpMessagingTemplate.convertAndSendToUser(loginMember.getUsername(), "/room", exceptionMessage);
        return Message.builder().build();
    }
}
