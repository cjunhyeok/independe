package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatHistoryResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRead;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatReadRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.dtos.SaveChatDto;
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
    private final ChatReadRepository chatReadRepository;

    @Override
    @Transactional
    public Long saveChat(SaveChatDto dto) {
        Member findSender = memberRepository.findById(dto.getSenderId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(dto.getReceiverId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        ChatRoom findChatRoom = chatRoomRepository.findById(dto.getChatRoomId()).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

        Chat chat = Chat.builder()
                .message(dto.getMessage())
                .member(findSender)
                .chatRoom(findChatRoom)
                .build();
        Chat savedChat = chatRepository.save(chat);

        // 읽음 정보도 추가해야된다.
        ChatRead chatRead = ChatRead.builder()
                .isRead(dto.getIsRead())
                .chat(savedChat)
                .member(findReceiver)
                .build();
        chatReadRepository.save(chatRead);

        return savedChat.getId();
    }

    @Override
    @Transactional
    public List<ChatHistoryResponse> findChatHistory(Long chatRoomId, Long memberId) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        List<Chat> chatHistory = chatRepository.findChatHistory(chatRoomId);

        List<ChatHistoryResponse> chatHistoryResponses = new ArrayList<>();

        for (Chat chat : chatHistory) {

            Member sender = chat.getMember();

            // 읽음처리 해줘야함 (이런걸 다른 쓰레드에서 해도 괜찮을거 같다 (IN QUERY 로 조지기?)
            // 상대방이 안읽은 메시지도 가져와야 한다.
            ChatRead findChatRead = chatReadRepository.findByChatId(chat.getId());
            if (sender.getId() != memberId) {
                // 채팅이 상대방이 보낸거일 때
                findChatRead.updateIsReadTrue();
            }
            ChatHistoryResponse chatHistoryResponse = ChatHistoryResponse.builder()
                    .chatId(chat.getId())
                    .senderNickname(sender.getNickname())
                    .senderId(sender.getId())
                    .isRead(findChatRead.getIsRead())
                    .message(chat.getMessage())
                    .createdDate(chat.getCreatedDate())
                    .build();

            chatHistoryResponses.add(chatHistoryResponse);
        }

        return chatHistoryResponses;
    }

    @Override
    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_NOT_FOUND));
    }
}
