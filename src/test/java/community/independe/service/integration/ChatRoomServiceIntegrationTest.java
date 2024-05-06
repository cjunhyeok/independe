package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.chat.ChatRoomsResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRead;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.chat.ChatRoomParticipant;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatReadRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomParticipantRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatRoomService;
import community.independe.util.SortedStringEditor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatRoomServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomParticipantRepository chatRoomParticipantRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatReadRepository chatReadRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("보낸사람, 받는사람 PK 를 이용해 채팅방을 저장한다.")
    void saveChatRoomTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);

        // when
        Long savedChatRoomId = chatRoomService.saveChatRoom(savedSender.getId(), savedReceiver.getId());

        // then
        ChatRoom findChatRoom = chatRoomRepository.findById(savedChatRoomId).get();
        assertThat(findChatRoom.getId()).isEqualTo(savedChatRoomId);
        assertThat(findChatRoom.getTitle())
                .isEqualTo(SortedStringEditor.createSortedString(savedSender.getId(), savedReceiver.getId()));
    }

    // todo 채팅방 저장 throw 테스트 작성

    @Test
    @DisplayName("회원 PK 를 이용해 채팅방 목록을 조회한다.")
    void findChatRoomsTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        ChatRoomParticipant senderParticipant = ChatRoomParticipant.builder().member(savedSender).chatRoom(savedChatRoom).build();
        chatRoomParticipantRepository.save(senderParticipant);
        ChatRoomParticipant receiverParticipant = ChatRoomParticipant.builder().member(savedReceiver).chatRoom(savedChatRoom).build();
        chatRoomParticipantRepository.save(receiverParticipant);
        Chat chat = Chat.builder().message("chat").chatRoom(savedChatRoom).member(savedSender).build();
        Chat savedChat = chatRepository.save(chat);
        Chat secondChat = Chat.builder().message("secondChat").chatRoom(savedChatRoom).member(savedSender).build();
        Chat savedSecondChat = chatRepository.save(secondChat);
        Chat thirdChat = Chat.builder().message("thirdChat").chatRoom(savedChatRoom).member(savedSender).build();
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
        List<ChatRoomsResponse> findChatRoomsResponse = chatRoomService.findChatRooms(savedReceiver.getId());

        // then
        assertThat(findChatRoomsResponse).hasSize(1);
        assertThat(findChatRoomsResponse.get(0).getUnReadCount()).isEqualTo(2);
        assertThat(findChatRoomsResponse.get(0).getLastMessage()).isEqualTo("thirdChat");
    }
}
