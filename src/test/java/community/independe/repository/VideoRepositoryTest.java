package community.independe.repository;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class VideoRepositoryTest {

    @Autowired
    private VideoRepository videoRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void saveTest() {
        // given
        Video video = Video.builder()
                .videoUrl("videoUrl")
                .videoMasterUrl("masterUrl")
                .materName("masterName")
                .videoTitle("realTitle")
                .independentPostType(IndependentPostType.COOK)
                .views(1)
                .build();

        // when
        Video savedVideo = videoRepository.save(video);

        // then
        assertThat(savedVideo.getVideoUrl()).isEqualTo(video.getVideoUrl());
    }

    @Test
    public void findAllByIndependentPostTypeTest() {
        // given
        for (int i = 0; i < 5; i++) {
            Video video = Video.builder()
                    .videoUrl("videoUrl" + i)
                    .videoMasterUrl("masterUrl" + i)
                    .materName("masterName" + i)
                    .videoTitle("realTitle" + i)
                    .independentPostType(IndependentPostType.COOK)
                    .views(i)
                    .build();
            Video savedVideo = videoRepository.save(video);
        }
        Video nextVideo = Video.builder()
                .videoUrl("videoUrl")
                .videoMasterUrl("masterUrl")
                .materName("masterName")
                .videoTitle("realTitle")
                .independentPostType(IndependentPostType.WASH)
                .views(1)
                .build();
        Video savedNextVideo = videoRepository.save(nextVideo);

        // when
        List<Video> findAllByIndependentPostType =
                videoRepository.findAllByIndependentPostType(IndependentPostType.COOK);

        // then
        assertThat(findAllByIndependentPostType.size()).isEqualTo(5);
        assertThat(findAllByIndependentPostType.get(4).getVideoUrl()).isEqualTo("videoUrl" + 0);
        assertThat(findAllByIndependentPostType.get(0).getVideoUrl()).isEqualTo("videoUrl" + 4);
    }

    @Test
    public void mainTest() {
        Video cookVideo = Video.builder()
                .videoTitle("CBUM")
                .videoUrl("https://www.youtube.com/embed/trM50_Rk-qc")
                .independentPostType(IndependentPostType.COOK)
                .views(10)
                .build();
        videoRepository.save(cookVideo);

        Video cookVideo2 = Video.builder()
                .videoTitle("oneDay")
                .videoUrl("https://www.youtube.com/embed/bLES_JyrmhQ")
                .independentPostType(IndependentPostType.COOK)
                .views(5)
                .build();
        videoRepository.save(cookVideo2);

        Video cleanVideo = Video.builder()
                .videoTitle("infinity challenge")
                .videoUrl("https://www.youtube.com/embed/FVf-2DdFX80")
                .independentPostType(IndependentPostType.CLEAN)
                .views(10)
                .build();
        videoRepository.save(cleanVideo);

        Video cleanVideo2 = Video.builder()
                .videoTitle("bang kok")
                .videoUrl("https://www.youtube.com/embed/8A4MwL_MiPE")
                .independentPostType(IndependentPostType.CLEAN)
                .views(2)
                .build();
        videoRepository.save(cleanVideo2);

        Video washVideo = Video.builder()
                .videoTitle("relaxMan")
                .videoUrl("https://www.youtube.com/embed/iYPFRvQ9Jr4")
                .independentPostType(IndependentPostType.WASH)
                .views(10)
                .build();
        videoRepository.save(washVideo);

        Video washVideo2 = Video.builder()
                .videoTitle("danuri")
                .videoUrl("https://www.youtube.com/embed/Cr3zIEMk9Nk")
                .independentPostType(IndependentPostType.WASH)
                .views(12)
                .build();
        videoRepository.save(washVideo2);

        List<Video> allForMain = videoRepository.findAllForMain();

        assertThat(allForMain.size()).isEqualTo(3);
        assertThat(allForMain.get(0).getVideoTitle()).isEqualTo("CBUM");
        assertThat(allForMain.get(1).getVideoTitle()).isEqualTo("infinity challenge");
        assertThat(allForMain.get(2).getVideoTitle()).isEqualTo("danuri");
    }
}
