package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.service.chat.ChatSessionService;
import community.independe.service.dtos.FindMemberDto;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class ChatSessionServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private ChatSessionService chatSessionService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MemberRepository memberRepository;
    private final String SOCKETSESSIONPREIX = "SOCKETSESSION : ";
    private final String CHATROOMSESSIONPREIX = "CHATROOMSESSION : ";

    @AfterEach
    public void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

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

    @Test
    @DisplayName("채팅방 PK 와 함께 저장된 회원 PK 를 삭제한다.")
    void leaveChatRoomTest() {
        // given
        Long memberId = 1L;
        Long chatRoomId = 1L;
        chatSessionService.enterChatRoom(memberId, chatRoomId);

        // when
        chatSessionService.leaveChatRoom(memberId, chatRoomId);

        // then
        Set<String> chatRoomMember = redisTemplate.opsForSet().members(CHATROOMSESSIONPREIX + chatRoomId.toString());
        assertThat(chatRoomMember).isEmpty();
    }

    @Test
    @DisplayName("채팅방 PK 와 함께 저장된 회원 PK 들을 조회한다.")
    void getChatRoomMembersTest() {
        // given
        Long memberId = 1L;
        Long secondMemberId = 2L;
        Long chatRoomId = 1L;
        chatSessionService.enterChatRoom(memberId, chatRoomId);
        chatSessionService.enterChatRoom(secondMemberId, chatRoomId);

        // when
        Set<String> chatRoomMembers = chatSessionService.getChatRoomMembers(chatRoomId.toString());

        // then
        assertThat(chatRoomMembers).hasSize(2);
        assertThat(chatRoomMembers).containsExactly(memberId.toString(), secondMemberId.toString());
    }

    @Test
    @DisplayName("sessionId 와 함께 회원 PK 를 저장한다.")
    void enterSocketSessionTest() {
        // given
        String sessionId = "session";
        Long memberId = 1L;

        // when
        chatSessionService.enterSocketSession(sessionId, memberId);

        // then
        String memberIdString = redisTemplate.opsForValue().get(SOCKETSESSIONPREIX + sessionId);
        assertThat(Long.valueOf(memberIdString)).isEqualTo(memberId);
    }

    @Test
    @DisplayName("sessionId 에 저장된 데이터를 삭제한다.")
    void removeSocketSessionTest() {
        // given
        String sessionId = "session";
        Long memberId = 1L;
        chatSessionService.enterSocketSession(sessionId, memberId);

        // when
        chatSessionService.removeSocketSession(sessionId);

        // then
        String memberIdString = redisTemplate.opsForValue().get(SOCKETSESSIONPREIX + sessionId);
        assertThat(memberIdString).isNull();
    }

    @Test
    @DisplayName("sessionId 에 저장된 회원 정보를 조회한다.")
    void getMemberSocketSessionTest() {
        // given
        String sessionId = "session";
        String username = "username";
        String nickname = "nickname";
        Member savedMember = createMember(username, nickname);
        Long memberId = savedMember.getId();
        chatSessionService.enterSocketSession(sessionId, memberId);

        // when
        FindMemberDto memberDto = chatSessionService.getMemberSocketSession(sessionId);

        // then
        assertThat(memberDto.getId()).isEqualTo(memberId);
        assertThat(memberDto.getUsername()).isEqualTo(username);
        assertThat(memberDto.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("세션에서 조회한 memberId 가 저장되지 않았으면 예외를 발생시킨다.")
    void getMemberSocketSessionFailTest() {
        // given
        String sessionId = "session";
        Long memberId = 1L;
        chatSessionService.enterSocketSession(sessionId, memberId);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatSessionService.getMemberSocketSession(sessionId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    private Member createMember(String username, String nickname) {
        Member member = Member
                .builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .email("email")
                .number("number")
                .build();
        return memberRepository.save(member);
    }
}
