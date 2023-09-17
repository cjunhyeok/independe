package community.independe.service;

import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatRoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
}