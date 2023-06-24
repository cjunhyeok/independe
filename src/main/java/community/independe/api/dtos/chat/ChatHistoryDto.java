package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatHistoryDto {

    private String senderNickname;
    private String receiverNickname;
    private String message;
    private LocalDateTime createdDate;
    private Boolean isRead;

    @Builder
    public ChatHistoryDto(String senderNickname, String receiverNickname, String message, LocalDateTime createdDate, Boolean isRead) {
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.message = message;
        this.createdDate = createdDate;
        this.isRead = isRead;
    }
}
