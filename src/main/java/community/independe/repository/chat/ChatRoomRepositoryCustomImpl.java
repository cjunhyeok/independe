package community.independe.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.chat.ChatRoom;
import jakarta.persistence.EntityManager;

import java.util.List;

import static community.independe.domain.chat.QChat.chat;
import static community.independe.domain.chat.QChatRoom.chatRoom;

public class ChatRoomRepositoryCustomImpl implements ChatRoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ChatRoomRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoom> findChatRoomsByMemberId(Long memberId) {
//        return queryFactory
//                .select(chatRoom)
//                .from(chat)
//                .join(chat.chatRoom, chatRoom)
//                .where(
//                        chat.sender.id.eq(memberId)
//                                .or(chat.receiver.id.eq(memberId)))
//                .groupBy(chatRoom.id)
//                .fetch();
        return null;
    }
}
