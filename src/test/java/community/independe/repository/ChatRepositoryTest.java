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
    }
}
