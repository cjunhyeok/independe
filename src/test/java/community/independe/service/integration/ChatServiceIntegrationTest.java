package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.chat.ChatHistoryResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRead;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatReadRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatService;
import community.independe.service.chat.dtos.SaveChatDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private ChatService chatService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatReadRepository chatReadRepository;

    @Test
    @DisplayName("회원, 채팅방 정보를 이용해 채팅을 저장한다.")
    void saveChatTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        SaveChatDto dto = SaveChatDto.builder()
                .message("message")
                .senderId(savedSender.getId())
                .chatRoomId(savedChatRoom.getId())
                .isRead(false)
                .receiverId(savedReceiver.getId())
                .build();

        // when
        Long savedChatId = chatService.saveChat(dto);

        // then
        Chat findChat = chatRepository.findById(savedChatId).get();
        ChatRead findChatRead = chatReadRepository.findByChatId(findChat.getId());
        assertThat(findChat.getId()).isEqualTo(savedChatId);
        assertThat(findChat.getMessage()).isEqualTo(dto.getMessage());
        assertThat(findChatRead.getIsRead()).isFalse();
        assertThat(findChatRead.getMember()).isEqualTo(savedReceiver);
    }

    @Test
    @DisplayName("채팅 내역을 조회한다.")
    void findChatHistoryTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        SaveChatDto dto = SaveChatDto.builder()
                .message("message")
                .senderId(savedSender.getId())
                .chatRoomId(savedChatRoom.getId())
                .isRead(false)
                .receiverId(savedReceiver.getId())
                .build();
        Long savedChatId = chatService.saveChat(dto);
        SaveChatDto dto2 = SaveChatDto.builder()
                .message("message2")
                .senderId(savedSender.getId())
                .chatRoomId(savedChatRoom.getId())
                .isRead(false)
                .receiverId(savedReceiver.getId())
                .build();
        chatService.saveChat(dto);
        SaveChatDto dto3 = SaveChatDto.builder()
                .message("message3")
                .senderId(savedSender.getId())
                .chatRoomId(savedChatRoom.getId())
                .isRead(false)
                .receiverId(savedReceiver.getId())
                .build();
        chatService.saveChat(dto);

        // when
        List<ChatHistoryResponse> findChatHistory
                = chatService.findChatHistory(savedChatRoom.getId(), savedSender.getId());

        // then
        assertThat(findChatHistory).hasSize(3);
    }

    // todo 채팅 내역 조회 시 상대방이 보낸 데이터 로직 테스트

    @Test
    @DisplayName("채팅 PK 로 채팅 데이터를 조회한다.")
    void findByIdTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        Member receiver = Member.builder().username("receiver").password("pass").nickname("receiver").build();
        Member savedReceiver = memberRepository.save(receiver);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        SaveChatDto dto = SaveChatDto.builder()
                .message("message")
                .senderId(savedSender.getId())
                .chatRoomId(savedChatRoom.getId())
                .isRead(false)
                .receiverId(savedReceiver.getId())
                .build();
        Long savedChatId = chatService.saveChat(dto);

        // when
        Chat findChat = chatService.findById(savedChatId);

        // then
        assertThat(findChat.getId()).isEqualTo(savedChatId);
        assertThat(findChat.getMessage()).isEqualTo("message");
    }
}
