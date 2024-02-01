package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatHistoryResponse {

    private Long chatId;
    private String senderNickname;
    private String receiverNickname;
    private String message;
    private LocalDateTime createdDate;
    private Boolean isRead;
    private Long senderId;

    @Builder
    public ChatHistoryResponse(Long chatId, String senderNickname, String receiverNickname, String message, LocalDateTime createdDate, Boolean isRead) {
        this.chatId = chatId;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.message = message;
        this.createdDate = createdDate;
        this.isRead = isRead;
    }
}
