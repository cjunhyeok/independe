package community.independe.service;

import community.independe.api.dtos.post.main.*;
import community.independe.domain.post.Post;
import community.independe.domain.video.Video;
import community.independe.repository.main.MainPostApiRepository;
import community.independe.repository.video.VideoRepository;
import community.independe.service.dtos.main.MainPostPageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPostService {

    private final MainPostApiRepository repository;
    private final VideoRepository videoRepository;

    public List<PopularPostDto> findPopularPosts(MainPostPageRequest request) {

        List<Post> findAllPopularPosts = repository.findAllPopularPosts(request);

        return findAllPopularPosts.stream()
                .map(p -> new PopularPostDto(
                        p.getId(),
                        p.getTitle(),
                        (p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription(),
                        (p.getRegionType() == null) ? null : p.getRegionType().getDescription(),
                        (p.getRegionPostType() == null) ? null : p.getRegionPostType().getDescription(),
                        p.getIndependentPostType(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        p.getViews(),
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());
    }

    public List<PopularIndependentPostsDto> findIndependentPosts(MainPostPageRequest request) {

        List<Post> findAllIndependentPostByRecommendCount = repository.findAllIndependentPostByRecommendCount(request);

        return findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getIndependentPostType(),
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());
    }

    public List<RegionAllPostDto> findRegionAllPosts(MainPostPageRequest request) {

        List<Post> findAllRegionPostByRecommendCount = repository.findAllRegionAllPostByRecommendCount(request);

        return findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());
    }

    public List<RegionNotAllPostDto> findRegionNotAllPosts(MainPostPageRequest request) {

        List<Post> findRegionNotAllPostByRecommendCount = repository.findRegionNotAllPostByRecommendCount(request);

        return findRegionNotAllPostByRecommendCount.stream()
                .map(p -> new RegionNotAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRegionType().getDescription(),
                        p.getRegionPostType().getDescription(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());
    }

    public List<VideoMainDto> findAllForMain() {

        List<Video> findVideos = videoRepository.findAllForMain();

        return findVideos.stream()
                .map(v -> new VideoMainDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                )).collect(Collectors.toList());
    }
}
