package community.independe.api.dtos.chat;

import lombok.*;

@Getter
@NoArgsConstructor
public class ChatRoomsResponse {

    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String lastMessage;
    private Long unReadCount;

    @Builder
    public ChatRoomsResponse(Long chatRoomId, Long senderId, String senderNickname, String lastMessage, Long unReadCount) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.lastMessage = lastMessage;
        this.unReadCount = unReadCount;
    }
}
