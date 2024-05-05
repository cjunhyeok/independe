package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatService;
import community.independe.service.chat.dtos.SaveChatDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("회원, 채팅방 정보를 이용해 채팅을 저장한다.")
    void saveChatTest() {
        // given
        Member sender = Member.builder().username("sender").password("pass").nickname("sender").build();
        Member savedSender = memberRepository.save(sender);
        ChatRoom chatRoom = ChatRoom.builder().title("title").build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        SaveChatDto dto = SaveChatDto.builder()
                .message("message")
                .senderId(savedSender.getId())
                .chatRoomId(savedChatRoom.getId())
                .build();

        // when
        Long savedChatId = chatService.saveChat(dto);

        // then
        Chat findChat = chatRepository.findById(savedChatId).get();
        assertThat(findChat.getId()).isEqualTo(savedChatId);
        assertThat(findChat.getMessage()).isEqualTo(dto.getMessage());
    }
}
