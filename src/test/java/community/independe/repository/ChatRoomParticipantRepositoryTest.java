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
}
