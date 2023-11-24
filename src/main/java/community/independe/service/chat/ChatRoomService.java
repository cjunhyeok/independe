package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatRoomsResponse;
import community.independe.domain.chat.ChatRoom;

import java.util.List;

public interface ChatRoomService {

    Long saveChatRoom(Long senderId, Long receiverId);
    ChatRoom findBySenderAndReceiver(Long senderId, Long receiverId);
    List<ChatRoomsResponse> findChatRooms(Long memberId);
}
