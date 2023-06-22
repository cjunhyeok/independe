package community.independe.repository.chat;

import community.independe.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {

//    @Query(value = "select c from Chat c join fetch c.receiver" +
//            " join fetch c.sender" +
//            " where (c.sender.id = :loginMemberId and c.receiver.id = :receiverId)" +
//            " or (c.sender.id = :receiverId and c.receiver.id = :loginMemberId)" +
//            " order by c.createdDate asc",
//            countQuery = "select c from Chat c join fetch c.receiver" +
//                    " join fetch c.sender" +
//                    " where (c.sender.id = :loginMemberId and c.receiver.id = :receiverId)" +
//                    " or (c.sender.id = :receiverId and c.receiver.id = :loginMemberId)" +
//                    " order by c.createdDate desc")
//    List<Chat> findChatHistory(@Param("loginMemberId") Long loginMemberId,
//                               @Param("receiverId") Long receiverId);

    @Query(value = "select c from Chat c join fetch c.chatRoom cr" +
            " join fetch cr.sender crs" +
            " join fetch cr.receiver crr" +
            " where (cr.sender.id = :loginMemberId and cr.receiver.id = :receiverId)" +
            " or (cr.sender.id = :receiverId and cr.receiver.id = :loginMemberId)" +
            " order by c.createdDate asc",
    countQuery = "select c from Chat c join fetch c.chatRoom cr" +
            " join fetch cr.sender crs" +
            " join fetch cr.receiver crr" +
            " where (cr.sender.id = :loginMemberId and cr.receiver.id = :receiverId)" +
            " or (cr.sender.id = :receiverId and cr.receiver.id = :loginMemberId)" +
            " order by c.createdDate asc")
    List<Chat> findChatHistory(@Param("loginMemberId") Long loginMemberId,
                               @Param("receiverId") Long receiverId);
}
