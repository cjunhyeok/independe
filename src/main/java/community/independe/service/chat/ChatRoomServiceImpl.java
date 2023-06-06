package community.independe.service.chat;

import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService{

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public Long saveChatRoom(String title, Long senderId, Long receiverId) {

        Member findFirstMember = memberRepository.findById(senderId).orElseThrow(
                () -> new IllegalArgumentException("member not exist")
        );

        Member findSecondMember = memberRepository.findById(receiverId).orElseThrow(
                () -> new IllegalArgumentException("member not exist")
        );

        ChatRoom chatRoom = ChatRoom.builder()
                .title(title)
                .firstParticipation(findFirstMember)
                .secondParticipation(findSecondMember)
                .build();



        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return savedChatRoom.getId();
    }

    @Override
    public ChatRoom findByTitle(String title) {
        return chatRoomRepository.findByTitle(title);
    }

    @Override
    public ChatRoom findById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("chatRoom not exist")
        );
    }

    @Override
    public List<ChatRoom> findAllByLoginMember(Long loginMemberId) {
        return chatRoomRepository.findAllByLoginMemberId(loginMemberId);
    }

    @Override
    public ChatRoom findByLoginMemberIdWithReceiverId(Long loginMemberId, Long receiverId) {
        return chatRoomRepository.findByLoginMemberIdWithReceiverId(loginMemberId, receiverId);
    }
}
