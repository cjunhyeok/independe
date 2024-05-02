package community.independe.repository.chat;

import community.independe.domain.chat.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    @Query(value = "SELECT crp1" +
            " FROM ChatRoomParticipant crp1" +
            " JOIN ChatRoomParticipant crp2 ON crp1.chatRoom = crp2.chatRoom" +
            " WHERE crp1.member.id = :senderId AND crp2.member.id = :receiverId")
    ChatRoomParticipant findChatRoomParticipantsBySenderAndReceiverId(@Param("senderId") Long senderId,
                                                                           @Param("receiverId") Long receiverId);
}
