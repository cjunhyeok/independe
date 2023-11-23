package community.independe.service;

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
import community.independe.service.chat.ChatRoomServiceImpl;
import community.independe.util.SortedStringEditor;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ChatRepository chatRepository;

    @Test
    void saveChatRoomTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        Member sender = Member.builder().build();
        Member receiver = Member.builder().build();

        // stub
        when(memberRepository.findById(senderId)).thenAnswer(invocation -> {
            setPrivateField(sender, "id", senderId);
            Optional<Member> senderOptional = Optional.of(sender);
            return senderOptional;
        });
        when(memberRepository.findById(receiverId)).thenAnswer(invocation -> {
            setPrivateField(receiver, "id", receiverId);
            Optional<Member> receiverOptional = Optional.of(receiver);
            return receiverOptional;
        });
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(ChatRoom.builder().build());

        // when
        chatRoomService.saveChatRoom(senderId, receiverId);

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    void SortedStringEditorTest() {
        // given
        Long firstSenderId = 120546L;
        Long firstReceiverId = 85412564L;
        Long secondSenderId = 85412564L;
        Long secondReceiverId = 120546L;

        // when
        String firstString = SortedStringEditor.createSortedString(firstSenderId, firstReceiverId);
        String secondString = SortedStringEditor.createSortedString(secondSenderId, secondReceiverId);

        // then
        assertThat(firstString).isEqualTo(secondString);
    }

    @Test
    void saveChatRoomSenderFailTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatRoomService.saveChatRoom(senderId, receiverId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verifyNoMoreInteractions(memberRepository);
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void saveChatRoomReceiverFailTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(Member.builder().build()));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatRoomService.saveChatRoom(senderId, receiverId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void findBySenderAndReceiverTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        Member sender = Member.builder().build();
        Member receiver = Member.builder().build();
        String senderAndReceiver = SortedStringEditor.createSortedString(senderId, receiverId);

        // stub
        when(memberRepository.findById(senderId)).thenAnswer(invocation -> {
            setPrivateField(sender, "id", senderId);
            Optional<Member> senderOptional = Optional.of(sender);
            return senderOptional;
        });
        when(memberRepository.findById(receiverId)).thenAnswer(invocation -> {
            setPrivateField(receiver, "id", receiverId);
            Optional<Member> receiverOptional = Optional.of(receiver);
            return receiverOptional;
        });
        when(chatRoomRepository.findBySenderAndReceiver(senderAndReceiver)).thenReturn(ChatRoom.builder().senderAndReceiver(senderAndReceiver).build());

        // when
        ChatRoomResponse chatRoomResponse = chatRoomService.findBySenderAndReceiver(senderId, receiverId);

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRoomRepository, times(1)).findBySenderAndReceiver(senderAndReceiver);
    }

    @Test
    void findBySenderAndReceiverSenderFailTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatRoomService.findBySenderAndReceiver(senderId, receiverId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verifyNoMoreInteractions(memberRepository);
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void findBySenderAndReceiverReceiverFailTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(Member.builder().build()));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatRoomService.findBySenderAndReceiver(senderId, receiverId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void findChatRoomsTest() throws Exception {
        // given
        Long memberId = 1L;
        List<ChatRoom> chatRooms = new ArrayList<>();
        ChatRoom chatRoom = ChatRoom.builder().build();
        chatRooms.add(chatRoom);
        Member sender = Member.builder().build();
        setPrivateField(sender, "id", memberId);
        Member receiver = Member.builder().build();
        setPrivateField(receiver, "id", 2L);
        Chat lastChat = Chat.builder().chatRoom(chatRoom).isRead(false).message("lastMessage").sender(sender).receiver(receiver).build();

        // stub
        when(chatRoomRepository.findChatRoomsByMemberId(memberId)).thenReturn(chatRooms);
        when(chatRepository.findLastChatByChatRoomId(null)).thenReturn(lastChat);

        // when
        List<ChatRoomsResponse> chatRoomsResponse = chatRoomService.findChatRooms(memberId);

        // then
        assertThat(chatRoomsResponse.size()).isEqualTo(1);
        verify(chatRoomRepository, times(1)).findChatRoomsByMemberId(memberId);
        verify(chatRepository, times(1)).findLastChatByChatRoomId(null);
    }
}
