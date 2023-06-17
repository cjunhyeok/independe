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
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "chat_id")
    private Long id;
    private String content; // 메시지
    private Boolean isRead;
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    public Chat(String content, Boolean isRead, ChatRoom chatRoom) {
        this.content = content;
        this.isRead = isRead;
        this.chatRoom = chatRoom;
    }
}
