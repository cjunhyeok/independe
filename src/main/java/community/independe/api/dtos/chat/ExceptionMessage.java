package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionMessage {

    private String exceptionMessage;
    private Long chatRoomId;
    private Long chatId;

    @Builder
    public ExceptionMessage(String exceptionMessage, Long chatRoomId, Long chatId) {
        this.exceptionMessage = exceptionMessage;
        this.chatRoomId = chatRoomId;
        this.chatId = chatId;
    }
}
