package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.repository.emitter.EmitterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class EmitterRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private EmitterRepository emitterRepository;

    @AfterEach
    void tearDown() {
        emitterRepository.deleteAll();
    }

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

    @Test
    void deleteByIdTest() {
        // given
        Long id = 1L;
        SseEmitter sseEmitter = new SseEmitter();
        emitterRepository.save(id, sseEmitter);

        // when
        emitterRepository.deleteById(id);

        // then
        SseEmitter findSseEmitter = emitterRepository.findById(id);
        assertThat(findSseEmitter).isNull();
    }

    @Test
    void findByIdTest() {
        // given
        Long id = 1L;
        SseEmitter sseEmitter = new SseEmitter();
        emitterRepository.save(id, sseEmitter);

        // when
        SseEmitter findSseEmitter = emitterRepository.findById(id);

        // then
        assertThat(findSseEmitter).isEqualTo(sseEmitter);
    }

    @Test
    void findAllTest() {
        // given
        Long id1 = 1L;
        Long id2 = 2L;
        SseEmitter sseEmitter1 = new SseEmitter();
        SseEmitter sseEmitter2 = new SseEmitter();
        emitterRepository.save(id1, sseEmitter1);
        emitterRepository.save(id2, sseEmitter2);

        // when
        List<SseEmitter> findSseEmitters = emitterRepository.findAll();

        // then
        assertThat(findSseEmitters.size()).isEqualTo(2);
    }
}