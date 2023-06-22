package community.independe.repository.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;

import java.util.List;

public interface ChatRepositoryCustom {

    List<Chat> findChatRooms(Long memberId);

    Chat findTopByChatRoomOrderByDateDesc(ChatRoom chatRoom);
}
