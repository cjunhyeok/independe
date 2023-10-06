package community.independe.service;

import community.independe.repository.emitter.EmitterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmitterServiceTest {

    @InjectMocks
    private EmitterServiceImpl emitterService;
    @Mock
    private EmitterRepository emitterRepository;

    @Test
    void subscribeTest() {
        // given
        Long memberId = 1L;
        SseEmitter sseEmitter = new SseEmitter();

        // stub
        doNothing().when(emitterRepository).save(anyLong(), any(SseEmitter.class));
        when(emitterRepository.findById(memberId)).thenReturn(sseEmitter);

        // when
        SseEmitter subscribe = emitterService.subscribe(memberId);

        // then
        verify(emitterRepository, times(1)).save(anyLong(), any(SseEmitter.class));
        verify(emitterRepository, times(1)).findById(memberId);
        Assertions.assertThat(subscribe).isExactlyInstanceOf(SseEmitter.class);
    }

    @Test
    void subscribeFindFailTest() {
        // given
        Long memberId = 1L;
        SseEmitter sseEmitter = new SseEmitter();

        // stub
        doNothing().when(emitterRepository).save(anyLong(), any(SseEmitter.class));
        when(emitterRepository.findById(memberId)).thenReturn(null);

        // when
        SseEmitter subscribe = emitterService.subscribe(memberId);

        // then
        verify(emitterRepository, times(1)).save(anyLong(), any(SseEmitter.class));
        verify(emitterRepository, times(1)).findById(memberId);
        verifyNoMoreInteractions(emitterRepository);
    }
}
