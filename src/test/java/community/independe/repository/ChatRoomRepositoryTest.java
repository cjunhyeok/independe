package community.independe.repository;

import community.independe.domain.chat.ChatRoom;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.util.SortedStringEditor;
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

    @Test
    void findBySenderAndReceiverTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String senderAndReceiver = SortedStringEditor.createSortedString(senderId, receiverId);
        ChatRoom chatRoom = ChatRoom.builder().senderAndReceiver(senderAndReceiver).build();
        chatRoomRepository.save(chatRoom);

        // when
        ChatRoom findChatRoom = chatRoomRepository.findBySenderAndReceiver(senderAndReceiver);

        // then
        assertThat(findChatRoom.getSenderAndReceiver()).isEqualTo(senderAndReceiver);
    }
}
