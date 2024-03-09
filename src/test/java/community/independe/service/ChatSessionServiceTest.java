package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.service.chat.ChatSessionServiceImpl;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatSessionServiceTest {

    @InjectMocks
    private ChatSessionServiceImpl chatSessionService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private SetOperations setOperations;
    @Mock
    private ValueOperations valueOperations;
    @Mock
    private RedisOperations<String, String> operations;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void enterChatRoomTest() {
        // given
        Long memberId = 1L;
        Long chatRoomId = 1L;

        // stub
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.add("CHATROOMSESSION : " + chatRoomId.toString(), memberId.toString())).thenReturn(1L);

        // when
        chatSessionService.enterChatRoom(memberId, chatRoomId);

        // then
        verify(redisTemplate, times(1)).opsForSet();
        verify(setOperations, times(1)).add("CHATROOMSESSION : " + chatRoomId.toString(), memberId.toString());
    }

    @Test
    void leaveChatRoomTest() {
        // given
        Long memberId = 1L;
        Long chatRoomId = 1L;

        // stub
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.remove("CHATROOMSESSION : " + chatRoomId.toString(), memberId.toString())).thenReturn(1L);

        // when
        chatSessionService.leaveChatRoom(memberId, chatRoomId);

        // then
        verify(redisTemplate, times(1)).opsForSet();
        verify(setOperations, times(1)).remove("CHATROOMSESSION : " + chatRoomId.toString(), memberId.toString());
    }

    @Test
    void enterSocketSessionTest() {
        // given
        String sessionId = "sessionId";
        Long memberId = 1L;

        // stub
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set("SOCKETSESSION : " + sessionId, memberId.toString());

        // when
        chatSessionService.enterSocketSession(sessionId, memberId);

        // then
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set("SOCKETSESSION : " + sessionId, memberId.toString());
    }

    @Test
    void removeSocketSessionTest() {
        // given
        String sessionId = "sessionId";

        // stub
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getOperations()).thenReturn(operations);
        when(operations.delete("SOCKETSESSION : " + sessionId)).thenReturn(true);

        // when
        chatSessionService.removeSocketSession(sessionId);

        // then
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).getOperations();
        verify(operations, times(1)).delete("SOCKETSESSION : " + sessionId);
    }

    @Test
    void getMemberSocketSessionTest() {
        // given
        String sessionId = "sessionId";
        String memberId = "1";
        Member member = Member.builder().build();

        // stub
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("SOCKETSESSION : " + sessionId)).thenReturn(memberId);
        when(memberRepository.findById(Long.parseLong(memberId))).thenReturn(Optional.of(member));

        // when
        Member findMember = chatSessionService.getMemberSocketSession(sessionId);

        // then
        assertThat(findMember).isEqualTo(member);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get("SOCKETSESSION : " + sessionId);
        verify(memberRepository, times(1)).findById(Long.parseLong(memberId));
    }

    @Test
    void getMemberSocketSessionFailTest() {
        // given
        String sessionId = "sessionId";
        String memberId = "1";
        Member member = Member.builder().build();

        // stub
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("SOCKETSESSION : " + sessionId)).thenReturn(memberId);
        when(memberRepository.findById(Long.parseLong(memberId))).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatSessionService.getMemberSocketSession(sessionId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get("SOCKETSESSION : " + sessionId);
        verify(memberRepository, times(1)).findById(Long.parseLong(memberId));
    }
}
