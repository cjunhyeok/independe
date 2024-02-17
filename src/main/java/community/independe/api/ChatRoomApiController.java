package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.chat.*;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.security.service.MemberContext;
import community.independe.service.MemberService;
import community.independe.service.chat.ChatRoomService;
import community.independe.service.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final MemberService memberService;

    @PostMapping("/api/chat/room")
    @Operation(summary = "채팅방 생성 * (발신자와 송신자를 맵핑한 채팅방이 존재하면 저장된 채팅방 정보를 반환)")
    public Result chatRoom(@RequestBody ChatRoomRequest chatRoomRequest,
                           @AuthenticationPrincipal MemberContext memberContext) {
        Member loginMember = memberContext.getMember();
        Long senderId = loginMember.getId();
        Long opponentId = chatRoomRequest.getOpponentId();

        if (senderId.equals(opponentId)) {
            throw new CustomException(ErrorCode.Coincide_Sender_Receiver);
        }

        ChatRoom findChatRoom = chatRoomService.findBySenderAndReceiver(senderId, opponentId);

        if (findChatRoom == null) {
            chatRoomService.saveChatRoom(senderId, opponentId);
            findChatRoom = chatRoomService.findBySenderAndReceiver(senderId, opponentId);
        }

        ChatRoomResponse chatRoomResponse = ChatRoomResponse.builder()
                .chatRoomId(findChatRoom.getId())
                .build();

        return new Result(chatRoomResponse);
    }

    @GetMapping("/api/chat/rooms")
    @Operation(summary = "채팅방 목록 조회 *")
    public Result chatRooms(@AuthenticationPrincipal MemberContext memberContext) {
        Member loginMember = memberContext.getMember();

        List<ChatRoomsResponse> chatRoomsResponses = chatRoomService.findChatRooms(loginMember.getId());

        return new Result<>(chatRoomsResponses);
    }

    @GetMapping("/api/chat/history")
    @Operation(summary = "채팅 내역 조회 * ")
    public Result chatHistory(@RequestParam("chatRoomId") Long chatRoomId,
                              @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        List<ChatHistoryResponse> chatHistory = chatService.findChatHistory(chatRoomId, loginMember.getId());

        return new Result(chatHistory);
    }
}
