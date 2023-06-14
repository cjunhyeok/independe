package community.independe.repository.video;


import community.independe.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {

}
