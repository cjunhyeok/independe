package community.independe.service;

import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatRoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .isInstanceOf(CustomException.class);

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
                .isInstanceOf(CustomException.class);

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
                .isInstanceOf(CustomException.class);

        // then
        verify(chatRoomRepository, times(1)).findById(id);
    }

    @Test
    void findByTitleTest() {
        // given
        String title = "title";
        ChatRoom mockChatRoom = ChatRoom.builder().build();

        // stub
        when(chatRoomRepository.findByTitle(title)).thenReturn(mockChatRoom);

        // when
        ChatRoom findChatRoom = chatRoomService.findByTitle(title);

        // then
        assertThat(findChatRoom).isEqualTo(mockChatRoom);
        verify(chatRoomRepository, times(1)).findByTitle(title);
    }

    @Test
    void findAllByLoginMember() {
        // given
        Long loginMemberId = 1L;
        List<ChatRoom> mockChatRooms = new ArrayList<>();
        mockChatRooms.add(ChatRoom.builder().build());

        // stub
        when(chatRoomRepository.findAllByLoginMemberId(loginMemberId)).thenReturn(mockChatRooms);

        // when
        List<ChatRoom> findChatRooms = chatRoomService.findAllByLoginMember(loginMemberId);

        // then
        assertThat(findChatRooms).isEqualTo(mockChatRooms);
        verify(chatRoomRepository, times(1)).findAllByLoginMemberId(loginMemberId);
    }

    @Test
    void findByLoginMemberIdWithReceiverId() {
        // given
        Long loginMemberId = 1L;
        Long receiverId = 2L;
        ChatRoom mockChatRoom = ChatRoom.builder().build();

        // stub
        when(chatRoomRepository.findByLoginMemberIdWithReceiverId(loginMemberId, receiverId))
                .thenReturn(mockChatRoom);

        // when
        ChatRoom findChatRoom = chatRoomService.findByLoginMemberIdWithReceiverId(loginMemberId, receiverId);

        // then
        assertThat(findChatRoom).isEqualTo(mockChatRoom);
        verify(chatRoomRepository, times(1)).findByLoginMemberIdWithReceiverId(loginMemberId, receiverId);
    }
}
