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
    private Long receiverId;
    private Long chatRoomId;
    private String senderNickname;
    private LocalDateTime createdDate;
    private Boolean isRead;
    private Boolean isExceptionData;

    @Builder
    public Message(String message, Long chatId, Long receiverId, Long chatRoomId, String senderNickname, LocalDateTime createdDate) {
        this.message = message;
        this.chatId = chatId;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
        this.createdDate = createdDate;
        this.isRead = false;
        this.isExceptionData = false;
    }
}
