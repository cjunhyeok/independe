package community.independe.service.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Long saveChat(String message, Long senderId, Long receiverId) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Chat chat = Chat.builder()
                .message(message)
                .sender(findSender)
                .receiver(findReceiver)
                .isRead(false)
                .build();
        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }
}
