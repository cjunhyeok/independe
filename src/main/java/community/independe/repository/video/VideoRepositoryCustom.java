package community.independe.repository.video;

import community.independe.domain.video.Video;

import java.util.List;

public interface VideoRepositoryCustom {

    List<Video> findAllForMain();
}
