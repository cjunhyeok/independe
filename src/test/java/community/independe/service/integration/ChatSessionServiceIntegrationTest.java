package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.service.chat.ChatSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ChatSessionServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private ChatSessionService chatSessionService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final String SOCKETSESSIONPREIX = "SOCKETSESSION : ";
    private final String CHATROOMSESSIONPREIX = "CHATROOMSESSION : ";

    @Test
    @DisplayName("채팅방 PK 로 회원 PK 가 Redis 에 저장된다.")
    void enterChatRoomTest() {
        // given
        Long memberId = 1L;
        Long chatRoomId = 1L;

        // when
        chatSessionService.enterChatRoom(memberId, chatRoomId);

        // then
        Set<String> chatRoomMember = redisTemplate.opsForSet().members(CHATROOMSESSIONPREIX + chatRoomId.toString());
        assertThat(chatRoomMember).hasSize(1);
    }
}
