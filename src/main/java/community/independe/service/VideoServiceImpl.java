package community.independe.service;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;

    @Override
    public List<Video> findAllForMain() {
        return videoRepository.findAllForMain();
    }

    @Override
    public List<Video> findAllByIndependentPostType(IndependentPostType independentPostType) {
        return videoRepository.findAllByIndependentPostType(independentPostType);
    }
}
