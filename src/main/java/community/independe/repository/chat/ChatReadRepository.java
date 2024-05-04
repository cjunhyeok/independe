package community.independe.repository.chat;

import community.independe.domain.chat.ChatRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatReadRepository extends JpaRepository<ChatRead, Long> {

    @Query("SELECT COUNT (cre)" +
            " FROM ChatRead cre" +
            " JOIN Chat c ON cre.chat.id = c.id" +
            " JOIN ChatRoom cro ON c.chatRoom.id = cro.id" +
            " WHERE cro.id = :chatRoomId" +
            " AND cre.member.id = :memberId" +
            " AND cre.isRead = false")
    Long findUnReadCountByChatRoomIdAndMemberId(@Param("chatRoomId") Long chatRoomId,
                                            @Param("memberId") Long memberId);
}
