package community.independe.service.chat;

public interface ChatReadService {
    Long findUnReadCount(Long chatRoomId, Long memberId);
}
