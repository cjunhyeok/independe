package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Message {

    private String message;
    private Long receiverId;
    private Long chatRoomId;
    private String senderNickname;
    private LocalDateTime createdDate;

    @Builder
    public Message(String message, Long receiverId, Long chatRoomId, String senderNickname, LocalDateTime createdDate) {
        this.message = message;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
        this.createdDate = createdDate;
    }
}
