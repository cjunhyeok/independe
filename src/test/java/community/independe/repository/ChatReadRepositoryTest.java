package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRead;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatReadRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatReadRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private ChatReadRepository chatReadRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("채팅을 보낼 시 읽음 데이터를 저장한다.")
    void saveReadChatTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedSender).build();
        Chat savedChat = chatRepository.save(chat);

        ChatRead chatRead = ChatRead.builder().isRead(false).chat(savedChat).member(savedReceiver).build();

        // when
        ChatRead savedChatRead = chatReadRepository.save(chatRead);

        // then
        assertThat(savedChatRead.getChat()).isEqualTo(savedChat);
        assertThat(savedChatRead.getMember()).isEqualTo(savedReceiver);
        assertThat(savedChatRead.getIsRead()).isFalse();
    }
}
