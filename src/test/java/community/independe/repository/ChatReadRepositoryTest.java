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

    @Test
    @DisplayName("회원 PK 와 채팅방 PK를 이용해 읽지않은 메시지 개수를 조회한다.")
    void findUnReadCountByChatIdAndMemberIdTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedSender).build();
        Chat savedChat = chatRepository.save(chat);
        Chat secondChat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedSender).build();
        Chat savedSecondChat = chatRepository.save(secondChat);
        Chat thirdChat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedSender).build();
        Chat savedThirdChat = chatRepository.save(thirdChat);

        ChatRead chatRead = ChatRead.builder().isRead(true).chat(savedChat).member(savedReceiver).build();
        chatReadRepository.save(chatRead);
        ChatRead chatSenderRead = ChatRead.builder().isRead(true).chat(savedChat).member(savedSender).build();
        chatReadRepository.save(chatSenderRead);
        ChatRead secondChatRead = ChatRead.builder().isRead(false).chat(savedSecondChat).member(savedReceiver).build();
        chatReadRepository.save(secondChatRead);
        ChatRead secondChatSenderRead = ChatRead.builder().isRead(true).chat(savedSecondChat).member(savedSender).build();
        chatReadRepository.save(secondChatSenderRead);
        ChatRead thirdChatRead = ChatRead.builder().isRead(false).chat(savedThirdChat).member(savedReceiver).build();
        chatReadRepository.save(thirdChatRead);
        ChatRead thirdChatSenderRead = ChatRead.builder().isRead(true).chat(savedThirdChat).member(savedSender).build();
        chatReadRepository.save(thirdChatSenderRead);

        // when
        Long findUnReadCount
                = chatReadRepository.findUnReadCountByChatRoomIdAndMemberId(savedChatRoom.getId(), savedReceiver.getId());

        // then
        assertThat(findUnReadCount).isEqualTo(2);
    }
}
