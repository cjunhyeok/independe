package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.util.SortedStringEditor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatRoomRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    void saveTest() {
        // given
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();

        // when
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // then
        assertThat(savedChatRoom.getTitle()).isEqualTo(chatRoom.getTitle());
    }

}
