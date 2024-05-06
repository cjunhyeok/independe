package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatRoomService;
import community.independe.util.SortedStringEditor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatRoomServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRepository chatRepository;
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
}
