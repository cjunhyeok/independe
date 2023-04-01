package community.independe.repository.video;


import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {

    @Query(value = "select v from Video v" +
            " where v.independentPostType = :independentPostType")
    List<Video> findAllByIndependentPostType(@Param("independentPostType")IndependentPostType independentPostType);
}
