package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatHistoryResponse;

import java.util.List;

public interface ChatService {

    Long saveChat(String message, Long senderId, Long receiverId, Long chatRoomId);
    List<ChatHistoryResponse> findChatHistory(Long chatRoomId, Long memberId);
}
