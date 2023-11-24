package community.independe.service;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatServiceImpl;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @InjectMocks
    private ChatServiceImpl chatService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Test
    void saveChatTest() {
        // given
        String message = "message";
        Long senderId = 1L;
        Long receiverId = 2L;
        Long chatRoomId = 1L;
        Member sender = Member.builder().build();
        Member receiver = Member.builder().build();
        ChatRoom chatRoom = ChatRoom.builder().build();

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRepository.save(any(Chat.class))).thenReturn(Chat.builder().build());

        // when
        chatService.saveChat(message, senderId, receiverId, chatRoomId);

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRoomRepository, times(1)).findById(chatRoomId);
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void saveChatSenderFailTest() {
        // given
        String message = "message";
        Long senderId = 1L;
        Long receiverId = 2L;
        Long chatRoomId = 1L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatService.saveChat(message, senderId, receiverId, chatRoomId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verifyNoMoreInteractions(memberRepository);
        verifyNoInteractions(chatRoomRepository);
        verifyNoInteractions(chatRepository);
    }

    @Test
    void saveChatReceiverFailTest() {
        // given
        String message = "message";
        Long senderId = 1L;
        Long receiverId = 2L;
        Long chatRoomId = 1L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(Member.builder().build()));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatService.saveChat(message, senderId, receiverId, chatRoomId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verifyNoInteractions(chatRoomRepository);
        verifyNoInteractions(chatRepository);
    }

    @Test
    void saveChatChatRoomFailTest() {
        // given
        String message = "message";
        Long senderId = 1L;
        Long receiverId = 2L;
        Long chatRoomId = 1L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(Member.builder().build()));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.of(Member.builder().build()));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> chatService.saveChat(message, senderId, receiverId, chatRoomId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CHAT_ROOM_NOT_FOUND);
        });
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRoomRepository, times(1)).findById(chatRoomId);
        verifyNoInteractions(chatRepository);
    }
}
