package community.independe.repository;

import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveTest() {
        // given
        Member sender = Member.builder().build();
        memberRepository.save(sender);
        Member receiver = Member.builder().build();
        memberRepository.save(receiver);
        String title = "senderToReceiver";

        ChatRoom chatRoom = ChatRoom
                .builder()
                .title(title)
                .sender(sender)
                .receiver(receiver)
                .build();

        // when
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // then
        assertThat(savedChatRoom.getSender()).isEqualTo(sender);
        assertThat(savedChatRoom.getReceiver()).isEqualTo(receiver);
        assertThat(savedChatRoom.getTitle()).isEqualTo(title);
    }
}