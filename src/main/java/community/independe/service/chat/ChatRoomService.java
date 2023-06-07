package community.independe.service.chat;

import community.independe.domain.chat.ChatRoom;

import java.util.List;

public interface ChatRoomService {

    Long saveChatRoom(String title, Long senderId, Long receiverId);
    ChatRoom findByTitle(String title);
    ChatRoom findById(Long id);
    List<ChatRoom> findAllByLoginMember(Long loginMemberId);
    ChatRoom findByLoginMemberIdWithReceiverId(Long loginMemberId, Long receiverId);
}
