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
public class ChatRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    void saveTest() {
        // given
        Member sender = Member.builder().build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().senderAndReceiver(SortedStringEditor.createSortedString(savedSender.getId(), savedReceiver.getId())).build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().isRead(false).message("firstMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();

        // when
        Chat savedChat = chatRepository.save(chat);

        // then
        assertThat(savedChat).isEqualTo(chat);
        assertThat(savedChat.getId()).isEqualTo(chat.getId());
        assertThat(savedChat.getMessage()).isEqualTo(chat.getMessage());
        assertThat(savedChat.getIsRead()).isEqualTo(chat.getIsRead());
        assertThat(savedChat.getSender()).isEqualTo(chat.getSender());
        assertThat(savedChat.getReceiver()).isEqualTo(chat.getReceiver());
        assertThat(savedChat.getChatRoom()).isEqualTo(savedChatRoom);
    }

    @Test
    void findLastChatByChatRoomIdTest() {
        // given
        Member sender = Member.builder().build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().senderAndReceiver(SortedStringEditor.createSortedString(savedSender.getId(), savedReceiver.getId())).build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().isRead(false).message("firstMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedChat = chatRepository.save(chat);
        Chat lastChat = Chat.builder().isRead(false).message("lastMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedLastChat = chatRepository.save(lastChat);

        // when
        Chat findLastChat = chatRepository.findLastChatByChatRoomId(savedChatRoom.getId());

        // then
        assertThat(findLastChat).isEqualTo(savedLastChat);
    }

    @Test
    void findChatHistoryTest() {
        // given
        Member sender = Member.builder().build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().senderAndReceiver(SortedStringEditor.createSortedString(savedSender.getId(), savedReceiver.getId())).build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().isRead(false).message("firstMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedChat = chatRepository.save(chat);
        Chat secondChat = Chat.builder().isRead(false).message("secondMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedSecondChat = chatRepository.save(secondChat);

        // when
        List<Chat> chatHistory = chatRepository.findChatHistory(savedChatRoom.getId());

        // then
        assertThat(chatHistory.size()).isEqualTo(2);
    }

    @Test
    void findIsReadCountByChatRoomIdTest() {
        // given
        Member sender = Member.builder().build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().senderAndReceiver(SortedStringEditor.createSortedString(savedSender.getId(), savedReceiver.getId())).build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().isRead(false).message("firstMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedChat = chatRepository.save(chat);
        Chat secondChat = Chat.builder().isRead(false).message("secondMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedSecondChat = chatRepository.save(secondChat);
        Chat thirdChat = Chat.builder().isRead(true).message("secondMessage").sender(savedSender).receiver(savedReceiver).chatRoom(savedChatRoom).build();
        Chat savedThirdChat = chatRepository.save(thirdChat);
        Chat fourthChat = Chat.builder().isRead(false).message("secondMessage").sender(savedReceiver).receiver(savedSender).chatRoom(savedChatRoom).build();
        Chat savedFourthChat = chatRepository.save(fourthChat);

        // when
        List<Chat> findChat = chatRepository.findIsReadCountByChatRoomId(savedChatRoom.getId(), savedReceiver.getId());

        // then
        assertThat(findChat.size()).isEqualTo(2);
    }
}
