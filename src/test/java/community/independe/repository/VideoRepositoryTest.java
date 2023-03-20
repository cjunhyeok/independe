package community.independe.repository;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class VideoRepositoryTest {

    @Autowired
    private VideoRepository videoRepository;

    @PersistenceContext
    private EntityManager em;

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

        em.flush();
        em.clear();

        List<Video> allForMain = videoRepository.findAllForMain();

        System.out.println(allForMain);

        for (Video video : allForMain) {
            System.out.println("title : " + video.getVideoTitle());
            System.out.println("type : " + video.getIndependentPostType());
            System.out.println("views : " + video.getViews());
        }
    }
}
