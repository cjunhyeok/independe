package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.chat.ChatRoomParticipant;
import community.independe.domain.member.Member;
import community.independe.repository.chat.ChatRoomParticipantRepository;
import community.independe.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatRoomParticipantRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomParticipantRepository chatRoomParticipantRepository;

    @Test
    @DisplayName("채팅방 참여자를 저장한다.")
    void saveChatRoomParticipantTest() {
        // given
        Member sender = Member.builder().build();
        Member savedSender = memberRepository.save(sender);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        ChatRoomParticipant chatRoomParticipant
                = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(savedSender).build();

        // when
        ChatRoomParticipant savedChatRoomParticipant = chatRoomParticipantRepository.save(chatRoomParticipant);

        // then
        assertThat(savedChatRoomParticipant.getMember()).isEqualTo(savedSender);
        assertThat(savedChatRoomParticipant.getChatRoom()).isEqualTo(savedChatRoom);
    }

    @Test
    @DisplayName("보낸사람, 받는사람 PK 로 채팅방 참여 정보를 조회한다.")
    void findChatRoomParticipantsBySenderAndReceiverIdTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);

        Member third = Member.builder().username("third").password("pass").nickname("third").build();
        Member savedThird = memberRepository.save(third);

        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        ChatRoom extra = ChatRoom.builder().title("Extra").build();
        ChatRoom savedExtra = chatRoomRepository.save(extra);

        ChatRoomParticipant senderParticipate = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(savedSender).build();
        ChatRoomParticipant savedSenderParticipant = chatRoomParticipantRepository.save(senderParticipate);
        ChatRoomParticipant receiverParticipate = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(savedReceiver).build();
        ChatRoomParticipant savedReceiverParticipant = chatRoomParticipantRepository.save(receiverParticipate);

        ChatRoomParticipant firstExtraParticipate = ChatRoomParticipant.builder().chatRoom(savedExtra).member(savedReceiver).build();
        ChatRoomParticipant savedFirstExtraParticipate = chatRoomParticipantRepository.save(firstExtraParticipate);
        ChatRoomParticipant secondExtraParticipate = ChatRoomParticipant.builder().chatRoom(savedExtra).member(savedThird).build();
        ChatRoomParticipant savedSecondExtraParticipate = chatRoomParticipantRepository.save(secondExtraParticipate);

        // when
        ChatRoomParticipant findChatRoomParticipant
                = chatRoomParticipantRepository
                .findChatRoomParticipantsBySenderAndReceiverId(savedSender.getId(), savedReceiver.getId()).get();

        // then
        assertThat(findChatRoomParticipant.getChatRoom()).isEqualTo(savedChatRoom);
    }

    @Test
    @DisplayName("보낸사람, 받는사람 순서 변경 후 채팅방 참여 정보를 조회한다.")
    void findChatRoomMemberPKOppositeTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);

        Member third = Member.builder().username("third").password("pass").nickname("third").build();
        Member savedThird = memberRepository.save(third);

        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        ChatRoom extra = ChatRoom.builder().title("Extra").build();
        ChatRoom savedExtra = chatRoomRepository.save(extra);

        ChatRoomParticipant senderParticipate = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(savedSender).build();
        ChatRoomParticipant savedSenderParticipant = chatRoomParticipantRepository.save(senderParticipate);
        ChatRoomParticipant receiverParticipate = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(savedReceiver).build();
        ChatRoomParticipant savedReceiverParticipant = chatRoomParticipantRepository.save(receiverParticipate);

        ChatRoomParticipant firstExtraParticipate = ChatRoomParticipant.builder().chatRoom(savedExtra).member(savedReceiver).build();
        ChatRoomParticipant savedFirstExtraParticipate = chatRoomParticipantRepository.save(firstExtraParticipate);
        ChatRoomParticipant secondExtraParticipate = ChatRoomParticipant.builder().chatRoom(savedExtra).member(savedThird).build();
        ChatRoomParticipant savedSecondExtraParticipate = chatRoomParticipantRepository.save(secondExtraParticipate);

        // when
        ChatRoomParticipant findChatRoomParticipant
                = chatRoomParticipantRepository
                .findChatRoomParticipantsBySenderAndReceiverId(savedReceiver.getId(), savedSender.getId()).get();

        // then
        assertThat(findChatRoomParticipant.getChatRoom()).isEqualTo(savedChatRoom);
    }
}
