package community.independe.service;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;

import java.util.List;

public interface VideoService {

    List<Video> findAllForMain();

    List<Video> findAllByIndependentPostType(IndependentPostType independentPostType);
    List<Video> findAll();
}
