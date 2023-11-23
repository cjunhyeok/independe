package community.independe.service.chat;

import community.independe.domain.chat.ChatRoom;

public interface ChatRoomService {

    Long saveChatRoom(Long senderId, Long receiverId);
    ChatRoom findBySenderAndReceiver(Long senderId, Long receiverId);
}
