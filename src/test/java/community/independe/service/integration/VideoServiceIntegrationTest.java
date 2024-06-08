package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.post.IndependentPostVideoDto;
import community.independe.api.dtos.video.FindVideosResponse;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import community.independe.service.VideoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class VideoServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoRepository videoRepository;

    @Test
    @DisplayName("자취 타입으로 영상을 조회한다.")
    void findAllByIndependentPostType() {
        // given
        IndependentPostType cook = IndependentPostType.COOK;
        Video cookVideo = createVideo(cook, 100);
        Video cookVideo2 = createVideo(cook, 100);
        Video cookVideo3 = createVideo(cook, 100);
        Video washVideo = createVideo(IndependentPostType.WASH, 100);

        // when
        List<IndependentPostVideoDto> findVideos = videoService.findAllByIndependentPostType(cook);

        // then
        assertThat(findVideos).hasSize(3);
    }

    @Test
    @DisplayName("모든 영상을 조회한다.")
    void findAllTest() {
        // given
        IndependentPostType cook = IndependentPostType.COOK;
        Video cookVideo = createVideo(cook, 100);
        Video cookVideo2 = createVideo(cook, 100);
        Video cookVideo3 = createVideo(cook, 100);
        Video washVideo = createVideo(IndependentPostType.WASH, 100);

        // when
        List<FindVideosResponse> findVideos = videoService.findAll();

        // then
        assertThat(findVideos).hasSize(4);
    }

    private Video createVideo(IndependentPostType independentPostType, Integer views) {
        Video video = Video.builder()
                .videoTitle("title")
                .videoUrl("url")
                .materName("master")
                .independentPostType(independentPostType)
                .views(views)
                .build();
        return videoRepository.save(video);
    }
}
