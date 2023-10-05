package community.independe.service.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long saveChat(Long senderId, Long receiverId, String content, Boolean isRead) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        ChatRoom chatRoom = chatRoomRepository.findByLoginMemberIdWithReceiverId(findSender.getId(), findReceiver.getId());

        Chat chat = Chat.builder()
                .content(content)
                .isRead(isRead)
                .chatRoom(chatRoom)
                .build();

        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }

    @Override
    public List<Chat> findChatRooms(Long memberId) {

        Member findSender = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        return chatRepository.findChatRooms(findSender.getId());
    }

    @Override
    public List<Chat> findChatHistory(Long loginMemberId, Long receiverId) {
        return chatRepository.findChatHistory(loginMemberId, receiverId);
    }

    @Override
    public Chat findTopByChatRoomOrderByDateDesc(ChatRoom chatRoom) {
        return chatRepository.findTopByChatRoomOrderByDateDesc(chatRoom);
    }
}
