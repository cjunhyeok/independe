package community.independe.service;

import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatRoomServiceImpl;
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
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Test
    void saveChatRoomTest() {
        // given
        String title = "mockTitle";
        Long senderId = 1L;
        Long receiverId = 2L;
        Member mockSender = Member.builder().build();
        Member mockReceiver = Member.builder().build();

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(mockSender));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.of(mockReceiver));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(ChatRoom.builder().build());

        // when
        chatRoomService.saveChatRoom(title, senderId, receiverId);

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void saveChatRoomSenderFailTest() {
        // given
        String title = "mockTitle";
        Long senderId = 1L;
        Long receiverId = 2L;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> chatRoomService.saveChatRoom(title, senderId, receiverId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void saveChatRoomReceiverFailTest() {
        String title = "mockTitle";
        Long senderId = 1L;
        Long receiverId = 2L;
        Member mockSender = Member.builder().build();

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(mockSender));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> chatRoomService.saveChatRoom(title, senderId, receiverId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void findByIdTest() {
        // given
        Long id = 1L;
        ChatRoom chatRoom = ChatRoom.builder().build();

        // stub
        when(chatRoomRepository.findById(id)).thenReturn(Optional.of(chatRoom));

        // when
        ChatRoom findChatRoom = chatRoomService.findById(id);

        // then
        verify(chatRoomRepository, times(1)).findById(id);
        assertThat(findChatRoom).isEqualTo(chatRoom);
    }

    @Test
    void findByIdFailTest() {
        // given
        Long id = 1L;

        // stub
        when(chatRoomRepository.findById(id)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> chatRoomService.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("chatRoom not exist");

        // then
        verify(chatRoomRepository, times(1)).findById(id);
    }
}
