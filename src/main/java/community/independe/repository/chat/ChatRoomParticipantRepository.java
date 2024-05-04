package community.independe.repository.chat;

import community.independe.domain.chat.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    @Query(value = "SELECT crp1" +
            " FROM ChatRoomParticipant crp1" +
            " JOIN ChatRoomParticipant crp2 ON crp1.chatRoom = crp2.chatRoom" +
            " WHERE crp1.member.id = :senderId AND crp2.member.id = :receiverId")
    Optional<ChatRoomParticipant> findChatRoomParticipantsBySenderAndReceiverId(@Param("senderId") Long senderId,
                                                                               @Param("receiverId") Long receiverId);

    @Query(value = "SELECT crp" +
            " FROM ChatRoomParticipant crp" +
            " WHERE crp.member.id = :memberId")
    List<ChatRoomParticipant> findChatRoomParticipantsByMemberId(@Param("memberId") Long memberId);
}
