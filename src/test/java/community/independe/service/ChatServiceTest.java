package community.independe.service;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatServiceImpl;
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
public class ChatServiceTest {

    @InjectMocks
    private ChatServiceImpl chatService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void saveChatTest() {
        // given
        Long senderId = 1L;
        Member sender = Member.builder().build();
        Long receiverId = 2L;
        Member receiver = Member.builder().build();
        String content = "content";
        ChatRoom chatRoom = ChatRoom.builder().sender(sender).receiver(receiver).build();
        Boolean isRead = false;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(chatRoomRepository.findByLoginMemberIdWithReceiverId(null, null))
                .thenReturn(chatRoom);
        when(chatRepository.save(any(Chat.class))).thenReturn(Chat.builder().build());

        // when
        chatService.saveChat(senderId, receiverId, content, isRead);

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRoomRepository, times(1)).findByLoginMemberIdWithReceiverId(null, null);
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void saveChatSenderFailTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String content = "fail";
        Boolean isRead = false;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> chatService.saveChat(senderId, receiverId, content, isRead))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verifyNoInteractions(chatRoomRepository);
        verifyNoInteractions(chatRepository);
    }

    @Test
    void saveChatReceiverFailTest() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String content = "fail";
        Boolean isRead = false;

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(Member.builder().build()));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> chatService.saveChat(senderId, receiverId, content, isRead))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verifyNoInteractions(chatRoomRepository);
        verifyNoInteractions(chatRepository);
    }

    @Test
    void findChatRoomsTest() {
        // given
        Long memberId = 1L;
        Member mockMember = Member.builder().build();
        List<Chat> chats = new ArrayList<>();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(chatRepository.findChatRooms(null)).thenReturn(chats);

        // when
        List<Chat> chatRooms = chatService.findChatRooms(memberId);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(chatRepository, times(1)).findChatRooms(null);
        assertThat(chatRooms).isEqualTo(chats);
    }

    @Test
    void findChatRoomsFailTest() {
        // given
        Long memberId = 1L;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> chatService.findChatRooms(memberId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(memberRepository, times(1)).findById(memberId);
    }
}
