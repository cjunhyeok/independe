package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatHistoryResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public Long saveChat(String message, Long senderId, Long receiverId, Long chatRoomId, Boolean isRead) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

        Chat chat = Chat.builder()
                .message(message)
                .sender(findSender)
                .receiver(findReceiver)
                .isRead(isRead)
                .chatRoom(findChatRoom)
                .build();
        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }

    @Override
    @Transactional
    public List<ChatHistoryResponse> findChatHistory(Long chatRoomId, Long memberId) {
        List<Chat> chatHistory = chatRepository.findChatHistory(chatRoomId);
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        List<ChatHistoryResponse> chatHistoryResponses = new ArrayList<>();

        for (Chat chat : chatHistory) {

            if (chat.getReceiver() == findMember) {
                chat.updateIsReadTrue();
            }

            ChatHistoryResponse chatHistoryResponse = new ChatHistoryResponse();
            chatHistoryResponse.setSenderNickname(chat.getSender().getNickname());
            chatHistoryResponse.setReceiverNickname(chat.getReceiver().getNickname());
            chatHistoryResponse.setMessage(chat.getMessage());
            chatHistoryResponse.setIsRead(chat.getIsRead());
            chatHistoryResponse.setCreatedDate(chat.getCreatedDate());
            chatHistoryResponses.add(chatHistoryResponse);
        }

        return chatHistoryResponses;
    }

    @Override
    @Transactional
    public void updateChatIsRead(Long chatId, Long chatRoomId, Long memberId) {
        Chat findChat = chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_NOT_FOUND)
        );

        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

        if (!findChat.getChatRoom().equals(findChatRoom)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_CHAT_NOT_MATCH);
        }

        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        if (findChat.getReceiver().equals(findMember)) {
            findChat.updateIsReadTrue();
        }
    }
}
