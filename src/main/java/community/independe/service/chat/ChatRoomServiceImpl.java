package community.independe.service.chat;

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
    public ChatRoom findBySenderAndReceiver(Long senderId, Long receiverId) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        String senderAndReceiver = SortedStringEditor.createSortedString(findSender.getId(), findReceiver.getId());

        ChatRoom findChatRoom = chatRoomRepository.findBySenderAndReceiver(senderAndReceiver);

        return findChatRoom;
    }

    @Override
    public List<ChatRoomsResponse> findChatRooms(Long memberId) {
        List<ChatRoom> findChatRooms = chatRoomRepository.findChatRoomsByMemberId(memberId);

        List<ChatRoomsResponse> chatRoomsResponses = new ArrayList<>();

        for (ChatRoom findChatRoom : findChatRooms) {
            Chat findLastChat = chatRepository.findLastChatByChatRoomId(findChatRoom.getId());
            List<Chat> findNonReadChat = chatRepository.findIsReadCountByChatRoomId(findChatRoom.getId(), memberId);
            Integer unReadCount = getIsReadCount(findNonReadChat);
            Long opponentId;
            String opponentNickname;

            if (findLastChat.getReceiver().getId() == memberId) {
                opponentId = findLastChat.getSender().getId();
                opponentNickname = findLastChat.getSender().getNickname();
            } else {
                opponentId = findLastChat.getReceiver().getId();
                opponentNickname = findLastChat.getReceiver().getNickname();
            }

            ChatRoomsResponse chatRoomsResponse = ChatRoomsResponse.builder()
                    .chatRoomId(findChatRoom.getId())
                    .receiverId(findLastChat.getReceiver().getId())
                    .senderNickname(findLastChat.getSender().getNickname())
                    .receiverNickname(findLastChat.getReceiver().getNickname())
                    .unReadCount(unReadCount)
                    .lastMessage(findLastChat.getMessage())
                    .opponentId(opponentId)
                    .opponentNickname(opponentNickname)
                    .build();
            chatRoomsResponses.add(chatRoomsResponse);
        }

        return chatRoomsResponses;
    }

    @Override
    public Integer findIsReadCountByChatRoomId(Long chatRoomId, Long memberId) {
        List<Chat> findChats = chatRepository.findIsReadCountByChatRoomId(chatRoomId, memberId);
        return getIsReadCount(findChats);
    }

    private Integer getIsReadCount(List<Chat> chats) {
        Integer isReadCount;
        if (chats.isEmpty()) {
            isReadCount = 0;
        } else {
            isReadCount = chats.size();
        }

        return isReadCount;
    }
}
