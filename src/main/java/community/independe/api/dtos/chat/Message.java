package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {

    private String message;
    private Long receiverId;
    private Long chatRoomId;
    private String senderNickname;

    @Builder
    public Message(String message, Long receiverId, Long chatRoomId, String senderNickname) {
        this.message = message;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
    }
}
