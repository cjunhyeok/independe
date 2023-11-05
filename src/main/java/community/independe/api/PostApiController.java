package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.*;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.comment.Comment;
import community.independe.domain.keyword.KeywordDto;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.query.PostApiRepository;
import community.independe.security.service.MemberContext;
import community.independe.service.*;
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
    private final PostApiRepository postApiRepository;
    private final FilesService filesService;
    private final RecommendPostService recommendPostService;
    private final FavoritePostService favoritePostService;
    private final ReportPostService reportPostService;
    private final RecommendCommentService recommendCommentService;

    // 자취 게시글 카테고리로 불러오기
    @Operation(summary = "자취 게시글 타입별 조회")
    @GetMapping("/api/posts/independent/{independentPostType}")
    public Result independentPosts(@PathVariable(name = "independentPostType") IndependentPostType independentPostType,
                                   @RequestParam(name = "condition", defaultValue = "no") String condition,
                                   @RequestParam(name = "keyword", required = false) String keyword,
                                   @PageableDefault(
                                           size = 10,
                                           sort = "createdDate",
                                           direction = Sort.Direction.DESC) Pageable pageable) {

        if (keyword == null || keyword.isEmpty()) {

        } else {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        // 게시글 불러오기
//        Page<Post> allIndependentPosts =
//                postService.findAllIndependentPostsByTypeWithMember(independentPostType, pageable);
        Page<Post> allIndependentPosts =
                postService.findAllIndependentPostsByTypeWithMember(independentPostType, condition, keyword, pageable);
        List<Post> independentPosts = allIndependentPosts.getContent();
        long totalCount = allIndependentPosts.getTotalElements();

        List<PostsResponse> postsCollect = independentPosts.stream()
                .map(p -> new PostsResponse(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
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
    @Operation(summary = "자취 게시글 생성 *")
    @PostMapping(value = "/api/posts/independent/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createIndependentPost(@Parameter(description = "제목") @RequestParam String title,
                                                      @Parameter(description = "내용") @RequestParam String content,
                                                      @Parameter(description = "자취 타입") @RequestParam IndependentPostType independentPostType,
                                                      @Parameter(description = "이미지") @RequestParam(required = false) List<MultipartFile> files,
                                                      @AuthenticationPrincipal MemberContext memberContext) throws IOException {

        Member loginMember = memberContext.getMember();
        Long memberId = loginMember.getId();

        Long independentPost = postService.createIndependentPost(
                memberId,
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
                              @PageableDefault(size = 10,
                                      sort = "createdDate",
                                      direction = Sort.Direction.DESC)Pageable pageable) {

        // 검색어 저장
        if (keyword == null || keyword.isEmpty()) {

        } else {
            keywordService.saveKeywordWithCondition(condition, keyword);
        }

        // 게시글 가져오기
//        Page<Post> allRegionPosts = postService.findAllRegionPostsByTypesWithMember(regionType, regionPostType, pageable);
        Page<Post> allRegionPosts = postService.findAllRegionPostsByTypesWithMember(regionType, regionPostType, condition, keyword, pageable);
        List<Post> regionPosts = allRegionPosts.getContent();
        long totalCount = allRegionPosts.getTotalElements();

        List<PostsResponse> collect = regionPosts.stream()
                .map(p -> new PostsResponse(
                        p.getId(),
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getCreatedDate(),
                        p.getViews(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                ))
                .collect(Collectors.toList());

        return new Result(collect, totalCount);
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

        Member loginMember = memberContext.getMember();
        Long memberId = loginMember.getId();

        Long regionPost = postService.createRegionPost(
                memberId,
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
//        if(files != null) {
//            filesService.saveFiles(files, updatedPost);
//        }

        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "게시글 삭제 *")
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity deletePost(@PathVariable Long postId,
                                     @AuthenticationPrincipal MemberContext memberContext) {

        postService.deletePost(postId);

        return ResponseEntity.ok("ok");
    }

    // 게시글 1개 구체정보 가져오기
    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/api/posts/{postId}")
    public Result post(@Parameter(description = "게시글 ID(PK)")@PathVariable(name = "postId") Long postId,
                       @AuthenticationPrincipal MemberContext memberContext) {

        Member member = null;

        if (memberContext == null) {
            member = null;
        } else {
            member = memberContext.getMember();
        }

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
                        isRecommendComment(c.getId(), findPost.getId(), (memberContext == null) ? null : memberContext.getMember())
                )).collect(Collectors.toList());

        // 게시글 Dto 생성
        PostResponse postResponse = new PostResponse(
                findPost,
                bestCommentDto,
                commentsDto,
                commentService.countAllByPostId(postId),
                recommendCount,
                isRecommend(findPost.getId(), member),
                isFavorite(findPost.getId(), member),
                isReport(findPost.getId(), member)
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

        if (keyword == null || keyword.isEmpty()) {

        } else {
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
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
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
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
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
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
                )).collect(Collectors.toList());

        // 전체 지역 게시글 5개
        List<Post> findAllRegionPostByRecommendCount = postApiRepository.findAllRegionAllPostByRecommendCount(yesterday, today, 0, 5);
        List<RegionAllPostDto> regionAllPostDto = findAllRegionPostByRecommendCount.stream()
                .map(p -> new RegionAllPostDto(
                        p.getId(),
                        p.getTitle(),
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
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
                        recommendPostService.countAllByPostIdAndIsRecommend(p.getId()),
                        commentService.countAllByPostId(p.getId()),
                        !filesService.findAllFilesByPostId(p.getId()).isEmpty()
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

    private boolean isRecommendComment(Long commentId, Long postId, Member member) {
        if (member == null) {
            return false;
        } else {
            if (recommendCommentService.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, member.getId()) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isRecommend(Long postId, Member member) {
        if(member == null) {
            return false;
        } else {
            if(recommendPostService.findByPostIdAndMemberIdAndIsRecommend(postId, member.getId()) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isFavorite(Long postId, Member member) {
        if(member == null) {
            return false;
        } else {
            if(favoritePostService.findByPostIdAndMemberIdAndIsRecommend(postId, member.getId()) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isReport(Long postId, Member member) {
        if(member == null) {
            return false;
        } else {
            if(reportPostService.findByPostIdAndMemberIdAndIsRecommend(postId, member.getId()) == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
