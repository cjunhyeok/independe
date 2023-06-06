package community.independe.repository.chat;

import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ChatRepositoryCustomImpl implements ChatRepositoryCustom{
    private final EntityManager em;

    public ChatRepositoryCustomImpl(EntityManager em) {
        this.em = em;
    }


    @Override
    public List<Chat> findChatRooms(Member member) {
        List<Chat> findChats = em.createQuery("select distinct c from Chat c" +
                        " join fetch c.receiver" +
                        " join fetch c.sender" +
                        " where c.sender = :member" +
                        " or c.receiver = :member", Chat.class)
                .setParameter("member", member)
                .getResultList();

        return findChats;
    }
}
