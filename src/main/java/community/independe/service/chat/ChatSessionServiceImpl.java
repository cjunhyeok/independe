package community.independe.service.chat;

import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService{

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final String SOCKETSESSIONPREIX = "SOCKETSESSION : ";

    @Override
    @Transactional
    public void enterChatRoom(Long memberId, Long chatRoomId) {
        redisTemplate.opsForSet().add(chatRoomId.toString(), memberId.toString()); // 채팅방 PK로 키 설정 후 세션 값 저장
    }

    @Override
    @Transactional
    public void leaveChatRoom(Long memberId, Long chatRoomId) {
        redisTemplate.opsForSet().remove(chatRoomId.toString(), memberId.toString()); // 채팅방 PK와 username 으로 세션 값 삭제
    }

    @Override
    @Transactional
    public Set<String> getChatRoomMembers(String chatRoomId) {
        return redisTemplate.opsForSet().members(chatRoomId);
    }

    @Override
    @Transactional
    public void enterSocketSession(String sessionId, Long memberId) {
        redisTemplate.opsForValue().set(SOCKETSESSIONPREIX +  sessionId, memberId.toString());
    }

    @Override
    @Transactional
    public void removeSocketSession(String sessionId) {
        redisTemplate.opsForValue().getOperations().delete(SOCKETSESSIONPREIX + sessionId);
    }

    @Override
    @Transactional
    public Member getMemberSocketSession(String sessionId) {
        String sessionMemberId = redisTemplate.opsForValue().get(SOCKETSESSIONPREIX + sessionId);
        return memberRepository.findById(Long.parseLong(sessionMemberId)).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
