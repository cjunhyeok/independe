package community.independe.service.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long saveChat(Long senderId, Long receiverId, String content, Boolean isRead) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new IllegalArgumentException("member not exist")
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new IllegalArgumentException("member not exist")
        );

        Chat chat = Chat.builder()
                .sender(findSender)
                .receiver(findReceiver)
                .content(content)
                .isRead(isRead)
                .build();

        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }

    @Override
    public List<Chat> findChatRooms(Long memberId) {

        Member findSender = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("member not exist")
        );

        return chatRepository.findChatRooms(findSender);
    }

    @Override
    public List<Chat> findChatHistory(Long loginMemberId, Long receiverId) {
        return chatRepository.findChatHistory(loginMemberId, receiverId);
    }
}
