package community.independe.repository;

import community.independe.repository.emitter.EmitterRepository;
import community.independe.repository.emitter.MemoryEmitterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.*;

@SpringJUnitConfig
@ContextConfiguration(classes = MemoryEmitterRepository.class)
public class EmitterRepositoryTest {

    @Autowired
    private EmitterRepository emitterRepository;

    @Test
    void saveTest() {
        // given
        Long id = 1L;
        SseEmitter sseEmitter = new SseEmitter();

        // when
        emitterRepository.save(id, sseEmitter);

        // then
        SseEmitter findSseEmitter = emitterRepository.findById(id);
        assertThat(findSseEmitter).isEqualTo(sseEmitter);
    }
}
