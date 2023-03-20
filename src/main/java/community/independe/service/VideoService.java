package community.independe.service;

import community.independe.domain.video.Video;

import java.util.List;

public interface VideoService {

    List<Video> findAllForMain();
}
