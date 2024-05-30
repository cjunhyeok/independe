package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.*;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.comment.Comment;
import community.independe.domain.keyword.KeywordDto;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.query.MainPostApiRepository;
import community.independe.security.service.MemberContext;
import community.independe.service.*;
import community.independe.service.dtos.post.FindIndependentPostRequest;
import community.independe.service.dtos.post.FindRegionPostRequest;
import community.independe.service.manytomany.FavoritePostService;
import community.independe.service.manytomany.RecommendCommentService;
import community.independe.service.manytomany.RecommendPostService;
import community.independe.service.manytomany.ReportPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final KeywordService keywordService;
    private final VideoService videoService;
    private final MainPostApiRepository mainPostApiRepository;
    private final FilesService filesService;
    private final RecommendPostService recommendPostService;
    private final FavoritePostService favoritePostService;
    private final ReportPostService reportPostService;
    private final RecommendCommentService recommendCommentService;

    // 자취 게시글 카테고리로 불러오기
    @Operation(summary = "자취 게시글 타입별 조회")
    @GetMapping("/api/posts/independent/{independentPostType}")
    public Result independentPosts(
            @RequestBody FindIndependentPostRequest request) {

        String keyword = request.getKeyword();
        String condition = request.getCondition();
        if (keyword != null && !keyword.isEmpty()) {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        // 게시글 불러오기
        List<PostsResponse> findPostsResponse
                = postService.findIndependentPosts(FindIndependentPostRequest.requestToFindDto(request));

        // 총 게시글 수
        Long totalCount = 0L;
        if (!findPostsResponse.isEmpty()) {
            totalCount = findPostsResponse.get(0).getTotalCount();
        }

        // 영상 불러오기
        List<Video> findAllByIndependentPostType = videoService.findAllByIndependentPostType(request.getIndependentPostType());
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
    public Result regionPosts(@RequestBody FindRegionPostRequest request) {

        // 검색어 저장
        String keyword = request.getKeyword();
        String condition = request.getCondition();
        if (keyword != null && !keyword.isEmpty()) {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        // 게시글 가져오기
        List<PostsResponse> findPostsResponse = postService.findRegionPosts(FindRegionPostRequest.requestToFindDto(request));

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

        // 증가 이후 찾기
        Post findPost = postService.findById(postId);
        List<Comment> findComments = commentService.findAllByPostId(postId);
        Long recommendCount = recommendPostService.countAllByPostIdAndIsRecommend(findPost.getId());

        // 베스트 댓글 찾기
        BestCommentDto bestCommentDto = null;
        List<Object[]> bestCommentList = recommendCommentService.findBestComment();
        if (bestCommentList.isEmpty()) {
            bestCommentDto = null;
        } else {
            Object[] bestCommentObject = bestCommentList.get(0);
            Comment bestComment = (Comment) bestCommentObject[0];
            Long bestCommentRecommendCount = (Long) bestCommentObject[1];
            bestCommentDto = new BestCommentDto(
                    bestComment.getId(),
                    bestComment.getMember().getNickname(),
                    bestComment.getContent(),
                    bestComment.getCreatedDate(),
                    bestCommentRecommendCount
            );
        }

        // 댓글 Dto 생성
        List<PostCommentResponse> commentsDto = findComments.stream()
                .map(c -> new PostCommentResponse(
                        c.getId(),
                        c.getMember().getNickname(),
                        c.getContent(),
                        c.getCreatedDate(),
                        recommendCommentService.countAllByCommentIdAndIsRecommend(c.getId()),
                        (c.getParent() == null) ? null : c.getParent().getId(),
                        c.getMember().getId(),
                        isRecommendComment(c.getId(), findPost.getId(), (memberContext == null) ? null : loginMemberId)
                )).collect(Collectors.toList());

        // 게시글 Dto 생성
        PostResponse postResponse = new PostResponse(
                findPost,
                bestCommentDto,
                commentsDto,
                commentService.countAllByPostId(postId),
                recommendCount,
                isRecommend(findPost.getId(), loginMemberId),
                isFavorite(findPost.getId(), loginMemberId),
                isReport(findPost.getId(), loginMemberId)
        );

        return new Result(postResponse);
    }

    @Operation(summary = "통합검색")
    @GetMapping("/api/posts/search")
    public Result searchPost(@RequestParam(name = "condition", defaultValue = "total") String condition,
                             @RequestParam(name = "keyword", required = false) String keyword,
                             @PageableDefault(size = 10,
                                     sort = "createdDate",
                                     direction = Sort.Direction.DESC)Pageable pageable) {

        if (keyword != null && !keyword.isEmpty()) {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        Page<Post> findAllPostsBySearchWithMember = postService.findAllPostsBySearchWithMember(condition, keyword, pageable);

        List<Post> posts = findAllPostsBySearchWithMember.getContent();
        long totalCount = findAllPostsBySearchWithMember.getTotalElements();

        List<SearchResponse> collect = posts.stream()
                .map(p -> new SearchResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getMember().getNickname(),
                        (p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription(),
                        (p.getRegionType() == null) ? null : p.getRegionType().getDescription(),
                        (p.getRegionPostType() == null) ? null : p.getRegionPostType().getDescription(),
                        p.getIndependentPostType(),
                        p.getRegionType(),
                        p.getRegionPostType(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).getS3Urls().isEmpty()
                ))
                .collect(Collectors.toList());

        return new Result(collect, totalCount);
    }

    @Operation(summary = "메인화면 조회")
    @GetMapping("/api/posts/main")
    public Result mainPost(HttpServletResponse response) {

        LocalDateTime today = LocalDateTime.now(); // 오늘
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1); // 어제
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        // 인기 게시글(10개)
        List<Post> findAllPopularPosts = mainPostApiRepository.findAllPopularPosts(yesterday, today, 0, 10);
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
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());

        // 추천수 자취 게시글 10개
        List<Post> findAllIndependentPostByRecommendCount = mainPostApiRepository.findAllIndependentPostByRecommendCount(yesterday, today, 0, 10);
        List<PopularIndependentPostsDto> popularIndependentPostsDto = findAllIndependentPostByRecommendCount.stream()
                .map(p -> new PopularIndependentPostsDto(
                        p.getId(),
                        p.getTitle(),
                        p.getIndependentPostType().getDescription(),
                        p.getIndependentPostType(),
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());

        // 전체 지역 게시글 5개
        List<Post> findAllRegionPostByRecommendCount = mainPostApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today, 0, 5);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        Long.valueOf(p.getRecommendPosts().size()),
                        Long.valueOf(p.getComments().size()),
                        (p.getFiles().isEmpty()) ? false : true
                )).collect(Collectors.toList());

        // 전체 아닌 지역 게시글 5개
        List<Post> findRegionNotAllPostByRecommendCount = mainPostApiRepository.findRegionNotAllPostByRecommendCount(yesterday, today, 0, 5);
        List<RegionNotAllPostDto> regionNotAllPostDto = findRegionNotAllPostByRecommendCount.stream()
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

        // 인기 검색어 10개
        List<KeywordDto> keywordsDto = keywordService.findKeywordsByGroup();

        // 영상
        List<Video> findAllForMain = videoService.findAllForMain();
        List<VideoMainDto> videoMainDto = findAllForMain.stream()
                .map(v -> new VideoMainDto(
                        v.getVideoTitle(),
                        v.getVideoUrl()
                )).collect(Collectors.toList());

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

    private boolean isRecommendComment(Long commentId, Long postId, Long memberId) {
        if (memberId == null) {
            return false;
        } else {
            if (recommendCommentService.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isRecommend(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            if(recommendPostService.findByPostIdAndMemberIdAndIsRecommend(postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isFavorite(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            if(favoritePostService.findByPostIdAndMemberIdAndIsRecommend(postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isReport(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            if(reportPostService.findByPostIdAndMemberIdAndIsRecommend(postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
