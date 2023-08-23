package community.independe.service;

import community.independe.repository.emitter.EmitterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        emitterRepository.save(memberId, sseEmitter);

        // when
        emitterService.subscribe(memberId);

        // then
        verify(emitterRepository, times(1)).save(memberId, sseEmitter);
    }
}
