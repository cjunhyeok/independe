package community.independe.service;

import community.independe.domain.post.enums.IndependentPostType;
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

    @Test
    void findAllByIndependentPostTypeTest() {
        // given
        IndependentPostType independentPostType = IndependentPostType.COOK;
        List<Video> videos = new ArrayList<>();
        videos.add(Video.builder().independentPostType(independentPostType).build());

        // stub
        when(videoRepository.findAllByIndependentPostType(independentPostType)).thenReturn(videos);

        // when
        List<Video> findVideos = videoService.findAllByIndependentPostType(independentPostType);

        // then
        assertThat(findVideos).isEqualTo(videos);
        verify(videoRepository, times(1)).findAllByIndependentPostType(independentPostType);
    }
}
