package community.independe.repository.video;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;

import java.util.List;

public interface VideoRepositoryCustom {

    List<Video> findAllForMain();
    List<Video> findAllByIndependentPostType(IndependentPostType independentPostType);
}
