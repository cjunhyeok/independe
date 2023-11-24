package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;

    @Builder
    public ChatRoomResponse(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
