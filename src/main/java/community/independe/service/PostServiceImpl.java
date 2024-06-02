package community.independe.service;

import community.independe.api.dtos.post.PostsResponse;
import community.independe.api.dtos.post.SearchResponse;
import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.file.FilesRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.util.PageRequestCreator;
import community.independe.service.dtos.MyPostServiceDto;
import community.independe.service.dtos.MyRecommendPostServiceDto;
import community.independe.service.dtos.post.FindAllPostsDto;
import community.independe.service.dtos.post.FindIndependentPostsDto;
import community.independe.service.dtos.post.FindPostDto;
import community.independe.service.dtos.post.FindRegionPostsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FilesRepository filesRepository;
    private final FilesService filesService;
    private final RecommendPostRepository recommendPostRepository;
    private static final String SORT = "createdDate";

    @Override
    public FindPostDto findById(Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return FindPostDto
                .builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .content(findPost.getContent())
                .independentPostType(findPost.getIndependentPostType())
                .regionType(findPost.getRegionType())
                .regionPostType(findPost.getRegionPostType())
                .views(findPost.getViews())
                .createdDate(findPost.getCreatedDate())
                .memberId(findPost.getMember().getId())
                .nickname(findPost.getMember().getNickname())
                .build();
    }

    // 자취 게시글 생성
    @Transactional
    @Override
    public Long createIndependentPost(Long memberId, String title, String content, IndependentPostType independentPostType) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .member(findMember)
                .independentPostType(independentPostType)
                .build();

        postRepository.save(post);
        return post.getId();
    }

    // 지역 게시글 생성
    @Override
    @Transactional
    public Long createRegionPost(Long memberId, String title, String content, RegionType regionType, RegionPostType regionPostType) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Post post = Post.builder()
                .title(title)
                .content(content)
                .member(findMember)
                .regionType(regionType)
                .regionPostType(regionPostType)
                .build();

        postRepository.save(post);
        return post.getId();
    }

    @Override
    @Transactional
    public Long updatePost(Long postId, String title, String content) {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        findPost.updatePost(title, content);

        return findPost.getId();
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post findPost = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        findPost.deleteMember();
        findPost.deleteRecommendPosts();

        filesService.deleteFile(postId);

        List<Comment> allByPostId = commentRepository.findAllByPostId(findPost.getId());
        for (Comment comment : allByPostId) {
            commentRepository.deleteCommentByParentId(comment.getId());
        }

        commentRepository.deleteCommentsByPostId(findPost.getId());
        postRepository.deletePostByPostId(findPost.getId());
    }

    @Override
    public List<PostsResponse> findIndependentPosts(FindIndependentPostsDto findIndependentPostsDto) {

        PageRequest pageRequest = PageRequestCreator.createPageRequestSortCreatedDateDesc(findIndependentPostsDto.getPage(), findIndependentPostsDto.getSize());

        Page<Post> findPostsPage = postRepository.findAllIndependentPostsByTypeWithMemberDynamic(
                        findIndependentPostsDto.getIndependentPostType(),
                        findIndependentPostsDto.getCondition(),
                        findIndependentPostsDto.getKeyword(),
                        pageRequest);

        List<Post> findPosts = findPostsPage.getContent();
        long totalCount = findPostsPage.getTotalElements();

        return findPosts.stream()
                .map(p -> PostsResponse
                        .builder()
                        .postId(p.getId())
                        .nickName(p.getMember().getNickname())
                        .title(p.getTitle())
                        .createdDate(p.getCreatedDate())
                        .views(p.getViews())
                        .recommendCount(recommendPostRepository.countAllByPostIdAndIsRecommend(p.getId()))
                        .commentCount(commentRepository.countAllByPostId(p.getId()))
                        .isPicture(!filesService.findAllFilesByPostId(p.getId()).getS3Urls().isEmpty())
                        .totalCount(totalCount)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<PostsResponse> findRegionPosts(FindRegionPostsDto findRegionPostsDto) {

        PageRequest pageRequest = PageRequestCreator.createPageRequestSortCreatedDateDesc(findRegionPostsDto.getPage(), findRegionPostsDto.getSize());

        Page<Post> findPostsPage = postRepository.findAllRegionPostsByTypesWithMemberDynamic(
                findRegionPostsDto.getRegionType(),
                findRegionPostsDto.getRegionPostType(),
                findRegionPostsDto.getCondition(),
                findRegionPostsDto.getKeyword(),
                pageRequest);

        List<Post> findPosts = findPostsPage.getContent();
        long totalCount = findPostsPage.getTotalElements();

        return findPosts.stream()
                .map(p -> PostsResponse
                        .builder()
                        .postId(p.getId())
                        .nickName(p.getMember().getNickname())
                        .title(p.getTitle())
                        .createdDate(p.getCreatedDate())
                        .views(p.getViews())
                        .recommendCount(recommendPostRepository.countAllByPostIdAndIsRecommend(p.getId()))
                        .commentCount(commentRepository.countAllByPostId(p.getId()))
                        .isPicture(!filesService.findAllFilesByPostId(p.getId()).getS3Urls().isEmpty())
                        .totalCount(totalCount)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchResponse> findAllPosts(FindAllPostsDto findAllPostsDto) {

        PageRequest pageRequest = PageRequestCreator.createPageRequestSortCreatedDateDesc(findAllPostsDto.getPage(), findAllPostsDto.getSize());

        Page<Post> findPostsPage = postRepository.findAllPostsBySearchWithMemberDynamic(
                        findAllPostsDto.getCondition(),
                        findAllPostsDto.getKeyword(),
                        pageRequest);

        List<Post> findPosts = findPostsPage.getContent();
        long totalCount = findPostsPage.getTotalElements();

        return findPosts.stream()
                .map(p -> SearchResponse
                        .builder()
                        .postId(p.getId())
                        .title(p.getTitle())
                        .nickname(p.getMember().getNickname())
                        .independentPostType((p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription())
                        .regionType((p.getIndependentPostType() == null) ? null : p.getIndependentPostType().getDescription())
                        .regionPostType((p.getRegionPostType() == null) ? null : p.getRegionPostType().getDescription())
                        .independentPostTypeEn(p.getIndependentPostType())
                        .regionTypeEn(p.getRegionType())
                        .regionPostTypeEn(p.getRegionPostType())
                        .views(p.getViews())
                        .recommendCount(recommendPostRepository.countAllByPostIdAndIsRecommend(p.getId()))
                        .commentCount(commentRepository.countAllByPostId(p.getId()))
                        .isPicture(!filesService.findAllFilesByPostId(p.getId()).getS3Urls().isEmpty())
                        .totalCount(totalCount)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void increaseViews(Long postId) {
        Post findPost = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        findPost.increaseViews(findPost.getViews() + 1);
    }

    @Override
    public List<MyPostServiceDto> findMyPost(Long memberId, int page, int size) {

        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(page, size);
        Page<Post> findPostPage = postRepository.findAllByMemberId(memberId, request);
        List<Post> findPosts = findPostPage.getContent();
        long totalCount = findPostPage.getTotalElements();

        List<MyPostServiceDto> myPostServiceDtos = findPosts.stream()
                .map(fp -> MyPostServiceDto.builder()
                        .postId(fp.getId())
                        .memberId(fp.getMember().getId())
                        .title(fp.getTitle())
                        .independentPostType(fp.getIndependentPostType())
                        .regionType(fp.getRegionType())
                        .regionPostType(fp.getRegionPostType())
                        .nickname(fp.getMember().getNickname())
                        .createdDate(fp.getCreatedDate())
                        .totalCount(totalCount)
                        .build()).collect(Collectors.toList());

        return myPostServiceDtos;
    }

    @Override
    public List<MyRecommendPostServiceDto> getMyRecommendPost(Long memberId, int page, int size) {

        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(page, size);
        Page<Post> recommendPostPage = postRepository.findRecommendPostByMemberId(memberId, request);
        List<Post> recommendPosts = recommendPostPage.getContent();
        long totalCount = recommendPostPage.getTotalElements();

        List<MyRecommendPostServiceDto> myRecommendPostServiceDtos = recommendPosts.stream()
                .map(rp -> MyRecommendPostServiceDto.builder()
                        .postId(rp.getId())
                        .memberId(rp.getMember().getId())
                        .title(rp.getTitle())
                        .independentPostType(rp.getIndependentPostType())
                        .regionType(rp.getRegionType())
                        .regionPostType(rp.getRegionPostType())
                        .nickname(rp.getMember().getNickname())
                        .createdDate(rp.getCreatedDate())
                        .totalCount(totalCount)
                        .build()).collect(Collectors.toList());

        return myRecommendPostServiceDtos;
    }
}
