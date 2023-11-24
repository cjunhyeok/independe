package community.independe.domain.chat;

import community.independe.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;
    private String senderAndReceiver;

    @Builder
    public ChatRoom(String senderAndReceiver) {
        this.senderAndReceiver = senderAndReceiver;
    }
}
