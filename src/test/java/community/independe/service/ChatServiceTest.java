package community.independe.service;

import community.independe.domain.chat.Chat;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.chat.ChatRepository;
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
    private MemberRepository memberRepository;

    @Test
    void saveChatTest() {
        // given
        String message = "message";
        Long senderId = 1L;
        Long receiverId = 2L;
        Member sender = Member.builder().build();
        Member receiver = Member.builder().build();

        // stub
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(chatRepository.save(any(Chat.class))).thenReturn(Chat.builder().build());

        // when
        chatService.saveChat(message, senderId, receiverId);

        // then
        verify(memberRepository, times(1)).findById(senderId);
        verify(memberRepository, times(1)).findById(receiverId);
        verify(chatRepository, times(1)).save(any(Chat.class));
    }
}
