package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatRoomResponse;
import community.independe.api.dtos.chat.ChatRoomsResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.util.SortedStringEditor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Long saveChatRoom(Long senderId, Long receiverId) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        ChatRoom chatRoom = ChatRoom.builder()
                .senderAndReceiver(SortedStringEditor.createSortedString(findSender.getId(), findReceiver.getId()))
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return savedChatRoom.getId();
    }

    @Override
    public ChatRoomResponse findBySenderAndReceiver(Long senderId, Long receiverId) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        String senderAndReceiver = SortedStringEditor.createSortedString(findSender.getId(), findReceiver.getId());

        ChatRoom findChatRoom = chatRoomRepository.findBySenderAndReceiver(senderAndReceiver);

        return ChatRoomResponse.builder()
                .chatRoomId(findChatRoom.getId())
                .build();
    }

    @Override
    public List<ChatRoomsResponse> findChatRooms(Long memberId) {
        List<ChatRoom> findChatRooms = chatRoomRepository.findChatRoomsByMemberId(memberId);

        List<ChatRoomsResponse> chatRoomsResponses = new ArrayList<>();

        for (ChatRoom findChatRoom : findChatRooms) {
            Chat findLastChat = chatRepository.findLastChatByChatRoomId(findChatRoom.getId());
            ChatRoomsResponse chatRoomsResponse = ChatRoomsResponse.builder()
                    .chatRoomId(findChatRoom.getId())
                    .senderNickname(findLastChat.getSender().getNickname())
                    .receiverNickname(findLastChat.getReceiver().getNickname())
                    .isRead(findLastChat.getIsRead())
                    .lastMessage(findLastChat.getMessage())
                    .build();
            chatRoomsResponses.add(chatRoomsResponse);
        }

        return chatRoomsResponses;
    }
}
