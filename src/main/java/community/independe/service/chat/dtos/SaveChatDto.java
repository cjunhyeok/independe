package community.independe.service.chat.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveChatDto {

    private String message;
    private Long chatRoomId;
    private Long senderId;

    @Builder
    public SaveChatDto(String message, Long chatRoomId, Long senderId) {
        this.message = message;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
    }
}
