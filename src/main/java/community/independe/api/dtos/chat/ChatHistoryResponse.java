package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatHistoryResponse {

    private Long chatId;
    private Long senderId;
    private String senderNickname;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdDate;

    @Builder
    public ChatHistoryResponse(Long chatId, Long senderId, String senderNickname, String message, Boolean isRead, LocalDateTime createdDate) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.message = message;
        this.isRead = isRead;
        this.createdDate = createdDate;
    }
}
