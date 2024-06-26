package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("채팅을 회원, 채팅방과 함께 저장한다.")
    void saveTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedMember).build();

        // when
        Chat savedChat = chatRepository.save(chat);

        // then
        assertThat(savedChat.getMember()).isEqualTo(savedMember);
        assertThat(savedChat.getChatRoom()).isEqualTo(savedChatRoom);
    }

    @Test
    @DisplayName("마지막 채팅 정보를 조회한다.")
    void findLastChatByChatRoomIdTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedMember).build();
        Chat savedChat = chatRepository.save(chat);
        Chat secondChat = Chat.builder().message("secondMessage").chatRoom(savedChatRoom).member(savedMember).build();
        Chat savedSecondChat = chatRepository.save(secondChat);
        Chat lastChat = Chat.builder().message("lastMessage").chatRoom(savedChatRoom).member(savedMember).build();
        Chat savedLastChat = chatRepository.save(lastChat);

        // when
        Chat findLastChat = chatRepository.findLastChatByChatRoomId(savedChatRoom.getId());

        // then
        assertThat(findLastChat.getMessage()).isEqualTo(lastChat.getMessage());
    }

    @Test
    @DisplayName("채팅방 PK 로 채팅 내역을 조회한다.")
    void findChatHistoryTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        Chat chat = Chat.builder().message("message").chatRoom(savedChatRoom).member(savedMember).build();
        Chat savedChat = chatRepository.save(chat);
        Chat secondChat = Chat.builder().message("secondMessage").chatRoom(savedChatRoom).member(savedMember).build();
        Chat savedSecondChat = chatRepository.save(secondChat);
        Chat lastChat = Chat.builder().message("lastMessage").chatRoom(savedChatRoom).member(savedMember).build();
        Chat savedLastChat = chatRepository.save(lastChat);

        // when
        List<Chat> findChatHistory = chatRepository.findChatHistory(savedChatRoom.getId());

        // then
        assertThat(findChatHistory).hasSize(3);
    }
}
