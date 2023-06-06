package community.independe.repository.chat;

import community.independe.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByTitle(String title);

    @Query(value = "select distinct cr from ChatRoom cr join fetch cr.firstParticipation" +
            " join fetch cr.secondParticipation" +
            " where cr.firstParticipation.id = :loginMemberId" +
            " or cr.secondParticipation.id = :loginMemberId",
            countQuery = "select cr from ChatRoom cr" +
                    " where cr.firstParticipation.id = :loginMemberId" +
                    " or cr.secondParticipation.id = :loginMemberId")
    List<ChatRoom> findAllByLoginMemberId(@Param("loginMemberId") Long loginMemberId);

    @Query(value = "select cr from ChatRoom cr" +
            " where (cr.firstParticipation.id = :loginMemberId and cr.secondParticipation.id = :receiverId)" +
            " or (cr.firstParticipation.id = :receiverId and cr.secondParticipation.id = :loginMemberId)",
            countQuery = "select cr from ChatRoom cr" +
                    " where (cr.firstParticipation.id = :loginMemberId and cr.secondParticipation.id = :receiverId)" +
                    " or (cr.firstParticipation.id = :receiverId and cr.secondParticipation.id = :loginMemberId)")
    ChatRoom findByLoginMemberIdWithReceiverId(@Param("loginMemberId") Long loginMemberId,
                                               @Param("receiverId") Long receiverId);
}
