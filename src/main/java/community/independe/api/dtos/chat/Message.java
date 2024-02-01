package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Message {

    private String message;
    private Long chatId;
    private Long opponentId;
    private Long chatRoomId;
    private String senderNickname;
    private LocalDateTime createdDate;
    private Boolean isRead;
    private Long senderId;

    @Builder
    public Message(String message, Long chatId, Long opponentId, Long chatRoomId, String senderNickname, LocalDateTime createdDate, Long senderId) {
        this.message = message;
        this.chatId = chatId;
        this.opponentId = opponentId;
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
        this.createdDate = createdDate;
        this.isRead = false;
        this.senderId = senderId;
    }
}
