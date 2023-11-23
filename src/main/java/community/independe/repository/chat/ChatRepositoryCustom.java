package community.independe.repository.chat;

import community.independe.domain.chat.Chat;

public interface ChatRepositoryCustom {

    Chat findLastChatByChatRoomId(Long chatRoomId);
}
