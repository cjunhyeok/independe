package community.independe.service;

import community.independe.api.dtos.post.IndependentPostVideoDto;
import community.independe.api.dtos.video.FindVideosResponse;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;

    @Override
    public List<IndependentPostVideoDto> findAllByIndependentPostType(IndependentPostType independentPostType) {
        List<Video> findAllByIndependentPostType = videoRepository.findAllByIndependentPostType(independentPostType);

        return findAllByIndependentPostType.stream()
                .map(v -> new IndependentPostVideoDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<FindVideosResponse> findAll() {
        List<Video> findVideos = videoRepository.findAll();

        return findVideos.stream()
                .map(v -> new FindVideosResponse(
                        v.getVideoUrl(),
                        v.getVideoMasterUrl(),
                        v.getMaterName(),
                        v.getVideoTitle(),
                        v.getIndependentPostType(),
                        v.getViews()
                )).collect(Collectors.toList());
    }
}
