package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequest {

    private Long receiverId;

    @Builder
    public ChatRoomRequest(Long receiverId) {
        this.receiverId = receiverId;
    }
}
