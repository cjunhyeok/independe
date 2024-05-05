package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequest {

    private Long opponentId;

    @Builder
    public ChatRoomRequest(Long opponentId) {
        this.opponentId = opponentId;
    }
}
