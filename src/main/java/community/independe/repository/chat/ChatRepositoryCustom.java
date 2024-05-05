package community.independe.repository.chat;

import community.independe.domain.chat.Chat;

import java.util.List;

public interface ChatRepositoryCustom {

    Chat findLastChatByChatRoomId(Long chatRoomId);
    List<Chat> findChatHistory(Long chatRoomId);
}
