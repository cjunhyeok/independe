package community.independe.repository;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.util.SortedStringEditor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MemberRepository memberRepository;

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

    @Test
    void findChatRoomsByMemberIdTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Member firstReceiver = Member.builder().build();
        Member savedFirstReceiver = memberRepository.save(firstReceiver);
        Member secondReceiver = Member.builder().build();
        Member savedSecondReceiver = memberRepository.save(secondReceiver);

        String firstSendAndReceive = SortedStringEditor.createSortedString(savedMember.getId(), savedFirstReceiver.getId());
        ChatRoom firstChatRoom = ChatRoom.builder().senderAndReceiver(firstSendAndReceive).build();
        ChatRoom savedFirstChatRoom = chatRoomRepository.save(firstChatRoom);

        String secondSendAndReceive = SortedStringEditor.createSortedString(savedMember.getId(), savedSecondReceiver.getId());
        ChatRoom secondChatRoom = ChatRoom.builder().senderAndReceiver(secondSendAndReceive).build();
        ChatRoom savedSecondChatRoom = chatRoomRepository.save(secondChatRoom);

        Chat firstChat = Chat.builder().sender(savedMember).receiver(savedFirstReceiver).chatRoom(savedFirstChatRoom).build();
        Chat savedFirstChat = chatRepository.save(firstChat);

        Chat secondChat = Chat.builder().sender(savedMember).receiver(savedSecondReceiver).chatRoom(savedSecondChatRoom).build();
        Chat savedSecondChat = chatRepository.save(secondChat);

        // when
        List<ChatRoom> findChatRooms = chatRoomRepository.findChatRoomsByMemberId(member.getId());

        // then
        assertThat(findChatRooms.size()).isEqualTo(2);
        assertThat(findChatRooms.get(0).getId()).isEqualTo(savedFirstChatRoom.getId());
        assertThat(findChatRooms.get(1).getId()).isEqualTo(savedSecondChatRoom.getId());
    }
}
