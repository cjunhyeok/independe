package community.independe.repository.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;

import java.util.List;

public interface ChatRepositoryCustom {

    List<Chat> findChatRooms(Member member);
}
