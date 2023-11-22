package community.independe.repository;

import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ChatRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChatRepository chatRepository;

    @Test
    void saveTest() {
        // given
        Member sender = Member.builder().build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().build();
        Member savedReceiver = memberRepository.save(receiver);
        Chat chat = Chat.builder().isRead(false).message("firstMessage").sender(savedSender).receiver(savedReceiver).build();

        // when
        Chat savedChat = chatRepository.save(chat);

        // then
        assertThat(savedChat).isEqualTo(chat);
        assertThat(savedChat.getId()).isEqualTo(chat.getId());
        assertThat(savedChat.getMessage()).isEqualTo(chat.getMessage());
        assertThat(savedChat.getIsRead()).isEqualTo(chat.getIsRead());
        assertThat(savedChat.getSender()).isEqualTo(chat.getSender());
        assertThat(savedChat.getReceiver()).isEqualTo(chat.getReceiver());
    }
}
