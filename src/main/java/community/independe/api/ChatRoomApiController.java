package community.independe.api;

import community.independe.api.dtos.chat.ChatHistoryDto;
import community.independe.api.dtos.chat.ChatRoomResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.MemberService;
import community.independe.service.chat.ChatRoomService;
import community.independe.service.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final ChatService chatService;

    @GetMapping("/api/chat/rooms")
    @Operation(description = "채팅방 목록 조회 *")
    public List<ChatRoomResponse> chatRooms(@AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        List<ChatRoom> findAllByLoginMember = chatRoomService.findAllByLoginMember(loginMember.getId());

        List<ChatRoomResponse> collect = findAllByLoginMember.stream().map(
                f -> ChatRoomResponse.builder()
                        .chatRoomId(f.getId())
                        .title(f.getTitle())
                        .receiverNickname(getOtherParticipationNickname(f, loginMember))
                        .receiverId(getOtherParticipationId(f, loginMember))
                        .lastMessage((chatService.findTopByChatRoomOrderByDateDesc(f) != null) ? chatService.findTopByChatRoomOrderByDateDesc(f).getContent() : null)
                        .build()
        ).collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/chat/room")
    @Operation(description = "채팅방 상세 조회 *")
    public ChatRoomResponse chatRoom(@RequestParam(name = "receiverId") Long receiverId,
                                     @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        ChatRoom findByLoginMemberIdWithReceiverId = chatRoomService.findByLoginMemberIdWithReceiverId(loginMember.getId(), receiverId);

        if (findByLoginMemberIdWithReceiverId == null) {
            Long savedChatRoom = chatRoomService.saveChatRoom(loginMember.getId() + "with" + receiverId, loginMember.getId(), receiverId);
            findByLoginMemberIdWithReceiverId = chatRoomService.findById(savedChatRoom);
        }

        return ChatRoomResponse.builder()
                .chatRoomId(findByLoginMemberIdWithReceiverId.getId())
                .myNickname(memberContext.getMember().getNickname())
                .build();
    }

    @GetMapping("/api/chat/history")
    @Operation(description = "채팅방 히스토리 조회 *")
    public List<ChatHistoryDto> chatHistory(@RequestParam("receiverId") Long receiverId,
                                            @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        List<Chat> chatHistory = chatService.findChatHistory(loginMember.getId(), receiverId);

        List<ChatHistoryDto> collect = chatHistory.stream().map(
                c -> ChatHistoryDto.builder()
                        .senderNickname(c.getChatRoom().getSender().getNickname())
                        .receiverNickname(c.getChatRoom().getReceiver().getNickname())
                        .message(c.getContent())
                        .createdDate(c.getCreatedDate())
                        .isRead(c.getIsRead())
                        .build()
        ).collect(Collectors.toList());

        return collect;
    }

    private String getOtherParticipationNickname(ChatRoom chatRoom, Member loginMember) {
        if (chatRoom.getSender().getId().equals(loginMember.getId())) {
            return chatRoom.getReceiver().getNickname();
        } else if (chatRoom.getReceiver().getId().equals(loginMember.getId())) {
            return chatRoom.getSender().getNickname();
        } else {
            throw new IllegalArgumentException("no participation");
        }
    }

    private Long getOtherParticipationId(ChatRoom chatRoom, Member loginMember) {
        if (chatRoom.getSender().getId().equals(loginMember.getId())) {
            return chatRoom.getReceiver().getId();
        } else if (chatRoom.getReceiver().getId().equals(loginMember.getId())) {
            return chatRoom.getSender().getId();
        } else {
            throw new IllegalArgumentException("no participation");
        }
    }
}
