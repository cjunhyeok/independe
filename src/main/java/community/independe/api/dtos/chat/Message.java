package community.independe.api.dtos.chat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Message {

    private Boolean isReadData; // 읽음 처리를 위한 데이터인지

    // 읽음 처리를 위한 데이터가 아닐경우
    private String message;

    // 채팅을 위한 데이터
    private Long chatId;
    private Long receiverId;
    private Long chatRoomId;
    private String senderNickname;
    private LocalDateTime createdDate;

    // 읽음 처리를 위한 데이터일 경우
    private Long isReadChatRoomId; // 채팅방 데이터 PK
    private Long isReadChatId; // 채팅 데이터 PK

    @Builder
    public Message(Boolean isReadData, String message, Long chatId, Long receiverId, Long chatRoomId, String senderNickname, LocalDateTime createdDate, Long isReadChatRoomId, Long isReadChatId) {
        this.isReadData = isReadData;
        this.message = message;
        this.chatId = chatId;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
        this.createdDate = createdDate;
        this.isReadChatRoomId = isReadChatRoomId;
        this.isReadChatId = isReadChatId;
    }
}
