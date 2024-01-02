package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Message {

    // 읽음 처리를 위한 데이터가 아닐경우
    private String message;

    // 채팅을 위한 데이터
    private Long chatId;
    private Long receiverId;
    private Long chatRoomId;
    private String senderNickname;
    private LocalDateTime createdDate;


    @Builder
    public Message(String message, Long chatId, Long receiverId, Long chatRoomId, String senderNickname, LocalDateTime createdDate) {
        this.message = message;
        this.chatId = chatId;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
        this.createdDate = createdDate;
    }
}
