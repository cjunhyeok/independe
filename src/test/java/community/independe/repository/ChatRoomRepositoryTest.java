package community.independe.repository;

import community.independe.domain.chat.ChatRoom;
import community.independe.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    void saveTest() {
        // given
        String senderAndReceiver = "senderIdAndReceiverId";
        ChatRoom chatRoom = ChatRoom.builder().senderAndReceiver(senderAndReceiver).build();

        // when
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // then
        assertThat(savedChatRoom).isEqualTo(chatRoom);
        assertThat(savedChatRoom.getId()).isEqualTo(chatRoom.getId());
        assertThat(savedChatRoom.getSenderAndReceiver()).isEqualTo(chatRoom.getSenderAndReceiver());
    }
}
