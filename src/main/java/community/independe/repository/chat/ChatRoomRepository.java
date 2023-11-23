package community.independe.repository.chat;

import community.independe.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(value = "select cr from ChatRoom cr" +
            " where cr.senderAndReceiver = :senderAndReceiver")
    ChatRoom findBySenderAndReceiver(@Param("senderAndReceiver") String senderAndReceiver);
}
