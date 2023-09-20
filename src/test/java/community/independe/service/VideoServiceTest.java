package community.independe.service;

import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    @InjectMocks
    private VideoServiceImpl videoService;
    @Mock
    private VideoRepository videoRepository;

    @Test
    void findAllForMainTest() {
        // given
        List<Video> videos = new ArrayList<>();
        videos.add(Video.builder().build());

        // stub
        when(videoRepository.findAllForMain()).thenReturn(videos);

        // when
        List<Video> findVideos = videoService.findAllForMain();

        // then
        assertThat(findVideos).isEqualTo(videos);
        verify(videoRepository, times(1)).findAllForMain();
    }
}
