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

        Optional<ChatRoomParticipant> findChatRoomParticipantOptional =
                chatRoomParticipantRepository
                .findChatRoomParticipantsBySenderAndReceiverId(findSender.getId(), findReceiver.getId());

        if (findChatRoomParticipantOptional.isPresent()) {

            ChatRoom findChatRoom = findChatRoomParticipantOptional.get().getChatRoom();
            // N + 1 발생
            return findChatRoom.getId();
        } else {
            // 채팅방 저장
            ChatRoom chatRoom = ChatRoom.builder()
                    .title(SortedStringEditor.createSortedString(findSender.getId(), findReceiver.getId()))
                    .build();
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

            // 채팅방 참여 저장
            ChatRoomParticipant senderChatRoomParticipant
                    = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(findSender).build();
            ChatRoomParticipant savedSenderChatRoomParticipant = chatRoomParticipantRepository.save(senderChatRoomParticipant);

            ChatRoomParticipant receiverChatRoomParticipant
                    = ChatRoomParticipant.builder().chatRoom(savedChatRoom).member(findReceiver).build();
            ChatRoomParticipant savedReceiverChatRoomParticipant = chatRoomParticipantRepository.save(receiverChatRoomParticipant);

            return savedChatRoom.getId();
        }
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
            // 채팅방 정보 가져온다.
            ChatRoom chatRoom = findChatRoomParticipant.getChatRoom();

            // 마지막 채팅 / 안읽은 채팅 개수 가져오기
            Chat findLastChat = chatRepository.findLastChatByChatRoomId(chatRoom.getId());
            Long findUnReadCount =
                    chatReadRepository.findUnReadCountByChatRoomIdAndMemberId(chatRoom.getId(), memberId);

            // 채팅방 참여 조회해 상대방 정보 찾기
            List<ChatRoomParticipant> findChatRoomParticipantsByChatRoom
                    = chatRoomParticipantRepository.findChatRoomParticipantsByChatRoomIdFetchMember(chatRoom.getId());

            ChatRoomsResponse.ChatRoomsResponseBuilder chatRoomsResponseBuilder = ChatRoomsResponse.builder()
                    .chatRoomId(findLastChat.getId())
                    .lastMessage(findLastChat.getMessage())
                    .unReadCount(findUnReadCount);

            // 상대방 정보 세팅
            for (ChatRoomParticipant chatRoomParticipant : findChatRoomParticipantsByChatRoom) {

                Member participantMember = chatRoomParticipant.getMember();

                // 채팅방 참여자 정보가 나(memberId) 와 다르면 상대방 정보 세팅
                if (!participantMember.getId().equals(memberId)) {
                    ChatRoomsResponse chatRoomsResponse = chatRoomsResponseBuilder
                            .opponentId(participantMember.getId())
                            .opponentNickname(participantMember.getNickname())
                            .build();
                    chatRoomsResponses.add(chatRoomsResponse);
                }
            }
        }

        return chatRoomsResponses;
    }
}
