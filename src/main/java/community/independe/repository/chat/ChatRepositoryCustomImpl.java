package community.independe.repository.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ChatRepositoryCustomImpl implements ChatRepositoryCustom{
    private final EntityManager em;

    public ChatRepositoryCustomImpl(EntityManager em) {
        this.em = em;
    }


    @Override
    public List<Chat> findChatRooms(Long memberId) {
//        List<Chat> findChats = em.createQuery("select distinct c from Chat c" +
//                        " join fetch c.receiver" +
//                        " join fetch c.sender" +
//                        " where c.sender = :member" +
//                        " or c.receiver = :member", Chat.class)
//                .setParameter("member", member)
//                .getResultList();

        List<Chat> findChats = em.createQuery("select distinct c from Chat c" +
                        " join fetch c.chatRoom cr" +
                        " join fetch cr.sender" +
                        " join fetch cr.receiver" +
                        " where cr.sender.id = :memberId" +
                        " or cr.receiver.id = :memberId", Chat.class)
                .setParameter("memberId", memberId)
                .getResultList();

        return findChats;
    }

    @Override
    public Chat findTopByChatRoomOrderByDateDesc(ChatRoom chatRoom) {
        TypedQuery<Chat> query = em.createQuery("select c from Chat c where c.chatRoom = :chatRoom" +
                        " and c.id = (select max(cc.id) from Chat cc where cc.chatRoom = c.chatRoom)", Chat.class);
        query.setParameter("chatRoom", chatRoom);

        List<Chat> resultList = query.getResultList();

        Chat lastMessage = null;
        if (!resultList.isEmpty()) {
            lastMessage = resultList.get(0);
        }

        return lastMessage;
    }
}
