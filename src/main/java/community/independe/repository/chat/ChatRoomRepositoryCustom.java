package community.independe.repository.chat;

import community.independe.domain.chat.ChatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> findChatRoomsByMemberId(Long memberId);
}
