package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.*;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.comment.Comment;
import community.independe.domain.file.Files;
import community.independe.domain.keyword.KeywordDto;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.query.PostApiRepository;
import community.independe.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final KeywordService keywordService;
    private final VideoService videoService;
    private final PostApiRepository postApiRepository;
    private final FilesService filesService;

    // 자취 게시글 카테고리로 불러오기
    @Operation(summary = "자취 게시글 타입별 조회")
    @GetMapping("/api/posts/independent/{type}")
    public Result independentPosts(@PathVariable(name = "type") IndependentPostType independentPostType,
                                   @PageableDefault(
                                           size = 10,
                                           sort = "createdDate",
                                           direction = Sort.Direction.DESC) Pageable pageable) {

        // 게시글 불러오기
        Page<Post> allIndependentPosts =
                postService.findAllIndependentPostsByTypeWithMember(independentPostType, pageable);
        List<Post> independentPosts = allIndependentPosts.getContent();
        long totalCount = allIndependentPosts.getTotalElements();

        List<PostsResponse> postsCollect = independentPosts.stream()
                .map(p -> new PostsResponse(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                ))
                .collect(Collectors.toList());

        // 영상 불러오기
        List<Video> findAllByIndependentPostType = videoService.findAllByIndependentPostType(independentPostType);
        List<IndependentPostVideoDto> videoCollect = findAllByIndependentPostType.stream()
                .map(v -> new IndependentPostVideoDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                ))
                .collect(Collectors.toList());

        PostsResponseDto postsResponseDto = new PostsResponseDto(
                postsCollect,
                videoCollect
        );

        return new Result(postsResponseDto, totalCount);
    }

    // 자취 게시글 생성
    @Operation(summary = "자취 게시글 생성")
    @PostMapping("/api/posts/independent/new")
    public ResponseEntity<Long> createIndependentPost(@Parameter(description = "제목") @RequestParam String title,
                                                      @Parameter(description = "내용") @RequestParam String content,
                                                      @Parameter(description = "자취 타입") @RequestParam IndependentPostType independentPostType,
                                                      @Parameter(description = "이미지") @RequestParam(required = false) List<MultipartFile> files,
                                                      @AuthenticationPrincipal Member member) throws IOException {

        Long independentPost = postService.createIndependentPost(
                member.getId(),
                title,
                content,
                independentPostType);

        filesService.saveFiles(files, independentPost);

        return ResponseEntity.ok(independentPost);
    }

    // 지역 게시글 카테고리 별로 가져오기
    @Operation(summary = "지역 게시글 타입별 조회")
    @GetMapping("/api/posts/region/{regionType}/{regionPostType}")
    public Result regionPosts(@PathVariable(name = "regionType") RegionType regionType,
                              @PathVariable(name = "regionPostType") RegionPostType regionPostType,
                              @PageableDefault(size = 10,
                                      sort = "createdDate",
                                      direction = Sort.Direction.DESC)Pageable pageable) {

        // 게시글 가져오기
        Page<Post> allRegionPosts = postService.findAllRegionPostsByTypesWithMember(regionType, regionPostType, pageable);
        List<Post> regionPosts = allRegionPosts.getContent();
        long totalCount = allRegionPosts.getTotalElements();

        List<PostsResponse> collect = regionPosts.stream()
                .map(p -> new PostsResponse(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                ))
                .collect(Collectors.toList());

        return new Result(collect, totalCount);
    }

    // 지역 게시글 생성
    @Operation(summary = "지역 게시글 생성")
    @PostMapping(value = "/api/posts/region/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createRegionPost(@Parameter(description = "제목") @RequestParam String title,
                                                 @Parameter(description = "내용") @RequestParam String content,
                                                 @Parameter(description = "지역 타입") @RequestParam RegionType regionType,
                                                 @Parameter(description = "지역 게시글 타입") @RequestParam RegionPostType regionPostType,
                                                 @Parameter(description = "이미지") @RequestParam(required = false) List<MultipartFile> files,
                                                 @AuthenticationPrincipal Member member) throws IOException {

        Long regionPost = postService.createRegionPost(
//                member.getId(),
                1L, // for test
                title,
                content,
                regionType,
                regionPostType
        );

        filesService.saveFiles(files, regionPost);

        return ResponseEntity.ok(regionPost);
    }

    // 게시글 1개 구체정보 가져오기
    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/api/posts/{postId}")
    public Result post(@Parameter(description = "게시글 ID(PK)")@PathVariable(name = "postId") Long postId) {

        postService.increaseViews(postId); // 조회수 증가

        // 증가 이후 찾기
        Post findPost = postService.findById(postId);
        List<Comment> findComments = commentService.findAllByPostId(postId);
        List<Files> findFiles = filesService.findAllFilesByPostId(postId);

        PostResponse postResponse = new PostResponse(findPost, findComments, findFiles, commentService.countAllByPostId(postId));
        return new Result(postResponse);
    }

//    @PostMapping("/api/posts/recommendCount/{postId}")
//    public ResponseEntity increaseOrDecreaseRecommendCount(@PathVariable(name = "postId") Long postId,
//                                                           @RequestBody Boolean isUp) {
//        postService.increaseOrDecreaseRecommendCount(postId, isUp);
//
//        return ResponseEntity.ok("Ok");
//    }

    @Operation(summary = "메인화면 조회")
    @GetMapping("/api/posts/main")
    public Result mainPost() {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        // 인기 게시글(10개)
        List<Post> findAllPopularPosts = postApiRepository.findAllPopularPosts(yesterday, today, 0, 10);
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
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 추천수 자취 게시글 10개
        List<Post> findAllIndependentPostByRecommendCount = postApiRepository.findAllIndependentPostByRecommendCount(yesterday, today, 0, 10);
        List<PopularIndependentPostsDto> popularIndependentPostsDto = findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getIndependentPostType(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 전체 지역 게시글 5개
        List<Post> findAllRegionPostByRecommendCount = postApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today, 0, 5);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 전체 아닌 지역 게시글 5개
        List<Post> findRegionNotAllPostByRecommendCount = postApiRepository.findRegionNotAllPostByRecommendCount(yesterday, today, 0, 5);
        List<RegionNotAllPostDto> regionNotAllPostDto = findRegionNotAllPostByRecommendCount.stream()
                .map(p -> new RegionNotAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        p.getRegionType().getDescription(),
                        p.getRegionPostType().getDescription(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 인기 검색어 10개
        List<KeywordDto> keywordDto = keywordService.findKeywordsByGroup();

        // 영상
        List<Video> findAllForMain = videoService.findAllForMain();
        List<VideoMainDto> videoMainDto = findAllForMain.stream()
                .map(v -> new VideoMainDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                )).collect(Collectors.toList());

        MainPostDto mainPostDto = new MainPostDto(
                "오늘은 힘드네요",
                popularPostDto,
                regionAllPostDto,
                regionNotAllPostDto,
                popularIndependentPostsDto,
                keywordDto,
                videoMainDto
        );

        return new Result(mainPostDto);
    }
}
