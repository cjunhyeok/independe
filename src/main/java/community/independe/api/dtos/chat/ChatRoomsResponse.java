package community.independe.api.dtos.chat;

import lombok.*;

@Getter
@NoArgsConstructor
public class ChatRoomsResponse {

    private Long chatRoomId;
    private Long opponentId; // 상대방 정보
    private String opponentNickname; // 상대방 닉네임
    private String lastMessage;
    private Long unReadCount;

    @Builder
    public ChatRoomsResponse(Long chatRoomId, Long opponentId, String opponentNickname, String lastMessage, Long unReadCount) {
        this.chatRoomId = chatRoomId;
        this.opponentId = opponentId;
        this.opponentNickname = opponentNickname;
        this.lastMessage = lastMessage;
        this.unReadCount = unReadCount;
    }
}
