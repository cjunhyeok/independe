package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReceiveMessage {

    private Long chatId;
    private String message;
    private Boolean isRead;
    private Long senderId;
    private String senderNickname;
    private LocalDateTime createdDate;

    @Builder
    public ReceiveMessage(Long chatId, String message, Boolean isRead, Long senderId, String senderNickname, LocalDateTime createdDate) {
        this.chatId = chatId;
        this.message = message;
        this.isRead = isRead;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.createdDate = createdDate;
    }
}
