package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatRoomResponse;

public interface ChatRoomService {

    Long saveChatRoom(Long senderId, Long receiverId);
    ChatRoomResponse findBySenderAndReceiver(Long senderId, Long receiverId);
}
