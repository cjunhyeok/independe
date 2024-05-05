package community.independe.service.chat;

import community.independe.api.dtos.chat.ChatRoomsResponse;
import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.chat.ChatRoomParticipant;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatReadRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomParticipantRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.util.SortedStringEditor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatReadRepository chatReadRepository;


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
                .title(SortedStringEditor.createSortedString(findSender.getId(), findReceiver.getId()))
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return savedChatRoom.getId();
    }

    @Override
    public Optional<ChatRoom> findBySenderAndReceiver(Long senderId, Long receiverId) {
        Member findSender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Member findReceiver = memberRepository.findById(receiverId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        ChatRoomParticipant chatRoomParticipant
                = chatRoomParticipantRepository.findChatRoomParticipantsBySenderAndReceiverId(findSender.getId(), findReceiver.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // chatRoom().getId() --> N + 1 쿼리 발생
        return chatRoomRepository.findById(chatRoomParticipant.getChatRoom().getId());
    }

    @Override
    public List<ChatRoomsResponse> findChatRooms(Long memberId) {

        List<ChatRoomsResponse> chatRoomsResponses = new ArrayList<>();

        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        List<ChatRoomParticipant> findChatRoomParticipants
                = chatRoomParticipantRepository.findChatRoomParticipantsByMemberId(findMember.getId());

        for (ChatRoomParticipant findChatRoomParticipant : findChatRoomParticipants) {
            ChatRoom chatRoom = findChatRoomParticipant.getChatRoom();

            Chat findLastChat = chatRepository.findLastChatByChatRoomId(chatRoom.getId());
            Long findUnReadCount =
                    chatReadRepository.findUnReadCountByChatRoomIdAndMemberId(chatRoom.getId(), memberId);

            ChatRoomsResponse chatRoomsResponse = ChatRoomsResponse.builder()
                    .chatRoomId(findLastChat.getId())
                    .lastMessage(findLastChat.getMessage())
                    .senderId(findLastChat.getMember().getId())
                    .senderNickname(findLastChat.getMember().getNickname())
                    .unReadCount(findUnReadCount)
                    .build();
            chatRoomsResponses.add(chatRoomsResponse);
        }

        return chatRoomsResponses;
    }
}
