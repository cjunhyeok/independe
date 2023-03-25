package community.independe.api.android;

import community.independe.api.android.dto.AndroidMainPostDto;
import community.independe.api.android.dto.TestDto;
import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.post.Post;
import community.independe.domain.video.Video;
import community.independe.repository.query.PostApiRepository;
import community.independe.service.CommentService;
import community.independe.service.PostService;
import community.independe.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostAndroidController {

    private final PostService postService;
    private final CommentService commentService;
    private final VideoService videoService;
    private final PostApiRepository postApiRepository;

    @GetMapping("/api/android/posts/main")
    public Result mainPost() {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제

        // 인기 게시글(10개)
        List<Post> findAllPopularPosts = postApiRepository.findAllPopularPosts(yesterday, today);
        List<PopularPostDto> popularPostDto = findAllPopularPosts.stream()
                .map(p -> new PopularPostDto(
                        p.getId(),
                        p.getTitle(),
                        (p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription(),
                        (p.getRegionType() == null) ? null : p.getRegionType().getDescription(),
                        (p.getRegionPostType() == null) ? null : p.getRegionPostType().getDescription(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        true
                )).collect(Collectors.toList());

        // 추천수 자취 게시글 10개
        List<Post> findAllIndependentPostByRecommendCount = postApiRepository.findAllIndependentPostByRecommendCount(yesterday, today);
        List<PopularIndependentPostsDto> popularIndependentPostsDto = findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                        , true
                )).collect(Collectors.toList());

        // 전체 지역 게시글 5개
        List<Post> findAllRegionPostByRecommendCount = postApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        false
                )).collect(Collectors.toList());

        // 전체 아닌 지역 게시글 5개
        List<Post> findRegionNotAllPostByRecommendCount = postApiRepository.findRegionNotAllPostByRecommendCount(yesterday, today);
        List<RegionNotAllPostDto> regionNotAllPostDto = findRegionNotAllPostByRecommendCount.stream()
                .map(p -> new RegionNotAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRegionType().getDescription(),
                        p.getRegionPostType().getDescription(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        true
                )).collect(Collectors.toList());

        // 영상
        List<Video> findAllForMain = videoService.findAllForMain();
        List<VideoMainDto> videoMainDto = findAllForMain.stream()
                .map(v -> new VideoMainDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                )).collect(Collectors.toList());

        AndroidMainPostDto mainPostDto = new AndroidMainPostDto(
                "오늘은 힘드네요",
                popularPostDto,
                regionAllPostDto,
                regionNotAllPostDto,
                popularIndependentPostsDto,
                videoMainDto
        );

        return new Result(mainPostDto);
    }

    @GetMapping("/api/android/test")
    public TestDto mainTest() {
        TestDto connection = new TestDto("connection");
        return connection;
    }
}
