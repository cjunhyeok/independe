package community.independe.service;

import community.independe.domain.chat.Chat;
import community.independe.domain.chat.ChatRoom;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
import community.independe.repository.chat.ChatRoomRepository;
import community.independe.service.chat.ChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
}
