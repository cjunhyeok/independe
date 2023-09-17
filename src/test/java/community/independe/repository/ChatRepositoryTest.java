package community.independe.repository;

import community.independe.domain.chat.Chat;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveTest() {
        // given
        Chat chat = Chat.builder().content("content").isRead(false).build();

        // when
        Chat savedChat = chatRepository.save(chat);

        // then
        Assertions.assertThat(savedChat).isEqualTo(chat);
    }
}
