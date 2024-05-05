package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatHistoryResponse;
import community.independe.domain.chat.Chat;
import community.independe.service.chat.dtos.SaveChatDto;

import java.util.List;

public interface ChatService {

    Long saveChat(SaveChatDto dto);
    List<ChatHistoryResponse> findChatHistory(Long chatRoomId, Long memberId);
    Chat findById(Long chatId);
}
