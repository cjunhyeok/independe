package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.*;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.keyword.KeywordDto;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.security.service.MemberContext;
import community.independe.service.*;
import community.independe.service.dtos.main.MainPostPageRequest;
import community.independe.service.dtos.post.*;
import community.independe.service.manytomany.RecommendCommentService;
import community.independe.service.manytomany.RecommendPostService;
import community.independe.service.util.ActionStatusChecker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final KeywordService keywordService;
    private final VideoService videoService;
    private final FilesService filesService;
    private final RecommendPostService recommendPostService;
    private final RecommendCommentService recommendCommentService;
    private final MainPostService mainPostService;
    private final ActionStatusChecker actionStatusChecker;

    // 자취 게시글 카테고리로 불러오기
    @Operation(summary = "자취 게시글 타입별 조회")
    @GetMapping("/api/posts/independent/{independentPostType}")
    public Result independentPosts(@PathVariable(name = "independentPostType") IndependentPostType independentPostType,
                                   @RequestParam(name = "condition", defaultValue = "no") String condition,
                                   @RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "page", defaultValue = "0") Integer page,
                                   @RequestParam(name = "size", defaultValue = "10") Integer size) {

        if (keyword != null && !keyword.isEmpty()) {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        FindIndependentPostsDto findIndependentPostsDto = FindIndependentPostsDto
                .builder()
                .independentPostType(independentPostType)
                .condition(condition)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        // 게시글 불러오기
        List<PostsResponse> findPostsResponse
                = postService.findIndependentPosts(findIndependentPostsDto);

        // 총 게시글 수
        Long totalCount = 0L;
        if (!findPostsResponse.isEmpty()) {
            totalCount = findPostsResponse.get(0).getTotalCount();
        }

        // 영상 불러오기
        List<Video> findAllByIndependentPostType = videoService.findAllByIndependentPostType(independentPostType);
        List<IndependentPostVideoDto> videoCollect = findAllByIndependentPostType.stream()
                .map(v -> new IndependentPostVideoDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                ))
                .collect(Collectors.toList());

        PostsResponseDto postsResponseDto = new PostsResponseDto(
                findPostsResponse,
                videoCollect
        );

        return new Result(postsResponseDto, totalCount);
    }

    // 자취 게시글 생성
    @Operation(summary = "자취 게시글 생성 *")
    @PostMapping(value = "/api/posts/independent/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createIndependentPost(@Parameter(description = "제목") @RequestParam String title,
                                                      @Parameter(description = "내용") @RequestParam String content,
                                                      @Parameter(description = "자취 타입") @RequestParam IndependentPostType independentPostType,
                                                      @Parameter(description = "이미지") @RequestParam(required = false) List<MultipartFile> files,
                                                      @AuthenticationPrincipal MemberContext memberContext) throws IOException {

        Long loginMemberId = memberContext.getMemberId();

        Long independentPost = postService.createIndependentPost(
                loginMemberId,
                title,
                content,
                independentPostType);

        if (files != null) {
            filesService.saveFiles(files, independentPost);
        }

        return ResponseEntity.ok(independentPost);
    }

    // 지역 게시글 카테고리 별로 가져오기
    @Operation(summary = "지역 게시글 타입별 조회")
    @GetMapping("/api/posts/region/{regionType}/{regionPostType}")
    public Result regionPosts(@PathVariable(name = "regionType") RegionType regionType,
                              @PathVariable(name = "regionPostType") RegionPostType regionPostType,
                              @RequestParam(name = "condition", defaultValue = "no") String condition,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "page", defaultValue = "0") Integer page,
                              @RequestParam(name = "size", defaultValue = "10") Integer size) {

        // 검색어 저장
        if (keyword != null && !keyword.isEmpty()) {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        FindRegionPostsDto findRegionPostsDto = FindRegionPostsDto
                .builder()
                .regionType(regionType)
                .regionPostType(regionPostType)
                .condition(condition)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        // 게시글 가져오기
        List<PostsResponse> findPostsResponse = postService.findRegionPosts(findRegionPostsDto);

        // 총 게시글 수
        Long totalCount = 0L;
        if (!findPostsResponse.isEmpty()) {
            totalCount = findPostsResponse.get(0).getTotalCount();
        }

        return new Result(findPostsResponse, totalCount);
    }

    // 지역 게시글 생성
    @Operation(summary = "지역 게시글 생성 *")
    @PostMapping(value = "/api/posts/region/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createRegionPost(@Parameter(description = "제목") @RequestParam String title,
                                                 @Parameter(description = "내용") @RequestParam String content,
                                                 @Parameter(description = "지역 타입") @RequestParam RegionType regionType,
                                                 @Parameter(description = "지역 게시글 타입") @RequestParam RegionPostType regionPostType,
                                                 @Parameter(description = "이미지") @RequestParam(required = false) List<MultipartFile> files,
                                                 @AuthenticationPrincipal MemberContext memberContext) throws IOException {

        Long loginMemberId = memberContext.getMemberId();

        Long regionPost = postService.createRegionPost(
                loginMemberId,
                title,
                content,
                regionType,
                regionPostType
        );

        if(files != null) {
            filesService.saveFiles(files, regionPost);
        }

        return ResponseEntity.ok(regionPost);
    }

    @Operation(summary = "게시글 수정 *")
    @PutMapping(value = "/api/posts/{postId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> updatePost(@Parameter(name = "postId") @PathVariable Long postId,
                                           @Parameter(description = "제목") @RequestParam String title,
                                           @Parameter(description = "내용") @RequestParam String content,
                                           @Parameter(description = "이미지") @RequestParam(required = false) List<MultipartFile> files,
                                           @AuthenticationPrincipal MemberContext memberContext) throws IOException {

        Long updatedPost = postService.updatePost(postId, title, content);

        // 파일 수정 기능 추가해야함
        if(files != null) {
            filesService.deleteFile(postId);
            filesService.saveFiles(files, updatedPost);
        }

        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "게시글 삭제 *")
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity deletePost(@PathVariable(name = "postId") Long postId,
                                     @AuthenticationPrincipal MemberContext memberContext) {

        // todo 요청하는 회원 정보와 일치하는지 확인

        postService.deletePost(postId);

        return ResponseEntity.ok("ok");
    }

    // 게시글 1개 구체정보 가져오기
    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/api/posts/{postId}")
    public Result post(@Parameter(description = "게시글 ID(PK)")@PathVariable(name = "postId") Long postId,
                       @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();

        postService.increaseViews(postId); // 조회수 증가

        // 증가 이후 게시글 조회
        FindPostDto findPostDto = postService.findById(postId);

        // 댓글
        List<PostCommentResponse> commentsDto = commentService.findCommentsByPostId(findPostDto.getId(), loginMemberId);
        Long recommendCount = recommendPostService.countAllByPostIdAndIsRecommend(findPostDto.getId());

        // 베스트 댓글 찾기
        BestCommentDto bestCommentDto = recommendCommentService.findBestComment();

        // 게시글 Dto 생성
        PostResponse postResponse = new PostResponse(
                findPostDto,
                bestCommentDto,
                commentsDto,
                commentService.countAllByPostId(postId),
                recommendCount,
                actionStatusChecker.isRecommend(postId, loginMemberId),
                actionStatusChecker.isFavorite(postId, loginMemberId),
                actionStatusChecker.isReport(postId, loginMemberId)
        );

        return new Result(postResponse);
    }

    @Operation(summary = "통합검색")
    @GetMapping("/api/posts/search")
    public Result searchPost(@RequestParam(name = "condition", defaultValue = "no") String condition,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             @RequestParam(name = "page", defaultValue = "0") Integer page,
                             @RequestParam(name = "size", defaultValue = "10") Integer size) {

        // 검색어 저장
        if (keyword != null && !keyword.isEmpty()) {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        FindAllPostsDto findAllPostsDto = FindAllPostsDto
                .builder()
                .condition(condition)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        List<SearchResponse> findSearchResponses = postService.findAllPosts(findAllPostsDto);

        // 총 게시글 수
        Long totalCount = 0L;
        if (!findSearchResponses.isEmpty()) {
            totalCount = findSearchResponses.get(0).getTotalCount();
        }

        return new Result(findSearchResponses, totalCount);
    }

    @Operation(summary = "메인화면 조회")
    @GetMapping("/api/posts/main")
    public Result mainPost(HttpServletResponse response) {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        MainPostPageRequest mainPostPageRequest = MainPostPageRequest
                .builder()
                .dateOffset(yesterday)
                .dateLimit(today)
                .offset(0)
                .limit(10)
                .build();

        // 인기 게시글(10개)
        List<PopularPostDto> popularPostDto = mainPostService.findPopularPosts(mainPostPageRequest);

        // 추천수 자취 게시글 10개
        List<PopularIndependentPostsDto> popularIndependentPostsDto = mainPostService.findIndependentPosts(mainPostPageRequest);

        mainPostPageRequest.updateLimit(5);

        // 전체 지역 게시글 5개
        List<RegionAllPostDto> regionAllPostDto = mainPostService.findRegionAllPosts(mainPostPageRequest);

        // 전체 아닌 지역 게시글 5개
        List<RegionNotAllPostDto> regionNotAllPostDto = mainPostService.findRegionNotAllPosts(mainPostPageRequest);

        // 인기 검색어 10개
        List<KeywordDto> keywordsDto = keywordService.findKeywordsByGroup();

        // 영상
        List<VideoMainDto> videoMainDto = mainPostService.findAllForMain();

        MainPostDto mainPostDto = new MainPostDto(
                "다가오는 장마철 천연 정화석을 구비하여 습기를 제거해보세요",
                popularPostDto,
                regionAllPostDto,
                regionNotAllPostDto,
                popularIndependentPostsDto,
                keywordsDto,
                videoMainDto
        );

        return new Result(mainPostDto);
    }
}
