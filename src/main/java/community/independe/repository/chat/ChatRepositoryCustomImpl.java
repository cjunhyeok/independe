package community.independe.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.chat.Chat;
import jakarta.persistence.EntityManager;

import java.util.List;

import static community.independe.domain.chat.QChat.chat;

public class ChatRepositoryCustomImpl implements ChatRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ChatRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Chat findLastChatByChatRoomId(Long chatRoomId) {
//        return queryFactory
//                .select(chat)
//                .from(chat)
//                .join(chat.sender).fetchJoin()
//                .join(chat.receiver).fetchJoin()
//                .where(chat.chatRoom.id.eq(chatRoomId))
//                .orderBy(chat.createdDate.desc())
//                .limit(1)
//                .fetchOne();
        return null;
    }

    @Override
    public List<Chat> findChatHistory(Long chatRoomId) {
//        return queryFactory
//                .select(chat)
//                .from(chat)
//                .join(chat.sender).fetchJoin()
//                .join(chat.receiver).fetchJoin()
//                .where(chat.chatRoom.id.eq(chatRoomId))
//                .orderBy(chat.createdDate.asc())
//                .fetch();
        return null;
    }

    @Override
    public List<Chat> findIsReadCountByChatRoomId(Long chatRoomId, Long memberId) {
//        return queryFactory
//                .select(chat)
//                .from(chat)
//                .join(chat.receiver).fetchJoin()
//                .where(chat.chatRoom.id.eq(chatRoomId)
//                        .and(chat.receiver.id.eq(memberId)
//                                .and(chat.isRead.isFalse())))
//                .fetch();
        return null;
    }
}
