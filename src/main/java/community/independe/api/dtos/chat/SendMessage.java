package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessage {

    private String message;
    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;

    @Builder
    public SendMessage(String message, Long chatRoomId, Long senderId, Long receiverId) {
        this.message = message;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
