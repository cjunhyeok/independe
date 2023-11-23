package community.independe.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.chat.Chat;
import jakarta.persistence.EntityManager;

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
        return queryFactory
                .select(chat)
                .from(chat)
                .where(chat.chatRoom.id.eq(chatRoomId))
                .orderBy(chat.createdDate.desc())
                .limit(1)
                .fetchOne();
    }
}
