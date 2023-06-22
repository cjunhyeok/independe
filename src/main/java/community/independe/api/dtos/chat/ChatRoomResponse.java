package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;
    private Long receiverId;
    private String receiverNickname;
    private String title;
    private String myNickname;
    private String lastMessage;

    @Builder
    public ChatRoomResponse(Long chatRoomId, Long receiverId, String receiverNickname, String title, String myNickname, String lastMessage) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.receiverNickname = receiverNickname;
        this.title = title;
        this.myNickname = myNickname;
        this.lastMessage = lastMessage;
    }
}
