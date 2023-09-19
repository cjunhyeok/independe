package community.independe.repository;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void initData() {
        Member sender = Member.builder().username("sender").build();
        memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").build();
        memberRepository.save(receiver);
        String title = "initSenderToReceiver";

        ChatRoom chatRoom = ChatRoom
                .builder()
                .title(title)
                .sender(sender)
                .receiver(receiver)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        Chat chat = Chat
                .builder()
                .chatRoom(savedChatRoom)
                .isRead(false)
                .content("initContent")
                .build();
        chatRepository.save(chat);
    }

    @Test
    void saveTest() {
        // given
        Member sender = Member.builder().username("sender").build();
        memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").build();
        memberRepository.save(receiver);
        String title = "senderToReceiver";

        ChatRoom chatRoom = ChatRoom
                .builder()
                .title(title)
                .sender(sender)
                .receiver(receiver)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        Chat chat = Chat.builder().content("content").isRead(false).chatRoom(chatRoom).build();

        // when
        Chat savedChat = chatRepository.save(chat);

        // then
        assertThat(savedChat).isEqualTo(chat);
        assertThat(savedChat.getId()).isEqualTo(chat.getId());
        assertThat(savedChat.getContent()).isEqualTo(chat.getContent());
        assertThat(savedChat.getIsRead()).isEqualTo(chat.getIsRead());
        assertThat(savedChat.getChatRoom()).isEqualTo(chat.getChatRoom());
    }

    @Test
    void findChatHistoryTest() {
        // given
        Member sender = memberRepository.findByUsername("sender");
        Member receiver = memberRepository.findByUsername("receiver");

        // when
        List<Chat> chatHistory = chatRepository.findChatHistory(sender.getId(), receiver.getId());

        // then
        assertThat(chatHistory.size()).isEqualTo(1);
        assertThat(chatHistory.get(0).getContent()).isEqualTo("initContent");
    }

    @Test
    void findChatRoomsTest() {
        // given
        Member sender = memberRepository.findByUsername("sender");

        // when
        List<Chat> chatRooms = chatRepository.findChatRooms(sender.getId());

        // then
        assertThat(chatRooms.size()).isEqualTo(1);
    }

    @Test
    void findTopByChatRoomOrderByDateDescTest() {
        // given
        ChatRoom chatRoom = chatRoomRepository.findByTitle("initSenderToReceiver");
        Chat lastChat = Chat.builder()
                .content("lastContent")
                .chatRoom(chatRoom)
                .isRead(false)
                .build();
        Chat savedLastChat = chatRepository.save(lastChat);

        // when
        Chat findChat = chatRepository.findTopByChatRoomOrderByDateDesc(chatRoom);

        // that
        assertThat(findChat).isEqualTo(savedLastChat);
    }
}
