package community.independe.api.android;

import community.independe.api.android.dto.AndroidMainPostDto;
import community.independe.api.android.dto.AndroidIndependentPostsResponse;
import community.independe.api.android.dto.TestDto;
import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import community.independe.repository.query.PostApiRepository;
import community.independe.service.CommentService;
import community.independe.service.PostService;
import community.independe.service.VideoService;
import community.independe.service.manytomany.RecommendPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final RecommendPostService recommendPostService;

    @GetMapping("/api/android/posts/independent/{independentPostType}")
    public Result androidIndependentPosts(@PathVariable(name = "independentPostType")IndependentPostType independentPostType,
                                          @PageableDefault(
                                           size = 10,
                                           sort = "createdDate",
                                           direction = Sort.Direction.DESC)Pageable pageable) {

        // 게시글 불러오기
        Slice allIndependentPostsSlice = postService.findAllIndependentPostsByTypeWithMember(independentPostType, pageable);

        // 현재 페이지에 나올 데이터 수
        int numberOfElements = allIndependentPostsSlice.getNumberOfElements();
        // 조회된 데이터
        List<Post> allIndependentPosts = allIndependentPostsSlice.getContent();
        // 다음 페이지 여부
        boolean hasLastPage = allIndependentPostsSlice.hasNext();
        // 마지막 페이지 여부
        boolean isLastPage = allIndependentPostsSlice.isLast();
        // 현재 페이지 여부
        boolean isFirstPage = allIndependentPostsSlice.isFirst();
        // 현재 페이지 넘버
        Integer pageNumber = pageable.getPageNumber();

        // 마지막 페이지가 아니고 다음 페이지가 있으면 다음 페이지 ++
        if (hasLastPage) {
            pageNumber++;
        }

        Integer finalPageNumber = pageNumber;
        List<AndroidIndependentPostsResponse> collect = allIndependentPosts.stream()
                .map(p -> new AndroidIndependentPostsResponse(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        numberOfElements,
                        hasLastPage,
                        isFirstPage,
                        isLastPage,
                        finalPageNumber
                )).collect(Collectors.toList());

        return new Result(collect);

    }

    @GetMapping("/api/android/posts/main")
    public Result androidMainPost() {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제

        // 인기 게시글(5개)
        List<Post> findAllPopularPosts = postApiRepository.findAllPopularPosts(yesterday, today, 0, 5);
        List<PopularPostDto> popularPostDto = findAllPopularPosts.stream()
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
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        true
                )).collect(Collectors.toList());

        // 추천수 자취 게시글 3개
        List<Post> findAllIndependentPostByRecommendCount = postApiRepository.findAllIndependentPostByRecommendCount(yesterday, today, 0, 3);
        List<PopularIndependentPostsDto> popularIndependentPostsDto = findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getIndependentPostType(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId())
                        , true
                )).collect(Collectors.toList());

        // 전체 지역 게시글 3개
        List<Post> findAllRegionPostByRecommendCount = postApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today, 0, 3);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        false
                )).collect(Collectors.toList());

        // 전체 아닌 지역 게시글 3개
        List<Post> findRegionNotAllPostByRecommendCount = postApiRepository.findRegionNotAllPostByRecommendCount(yesterday, today, 0, 3);
        List<RegionNotAllPostDto> regionNotAllPostDto = findRegionNotAllPostByRecommendCount.stream()
                .map(p -> new RegionNotAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRegionType().getDescription(),
                        p.getRegionPostType().getDescription(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
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
        log.info("in android main");
        TestDto connection = new TestDto("connection Test Ment");
        return connection;
    }
}
