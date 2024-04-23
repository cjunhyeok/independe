package community.independe.service;

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
import community.independe.repository.post.PostRepository;
import community.independe.repository.util.PageRequestCreator;
import community.independe.service.dtos.MyPostServiceDto;
import community.independe.service.dtos.MyRecommendPostServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
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
    public Page<Post> findAllIndependentPostsByTypeWithMember(IndependentPostType independentPostType, String condition, String keyword, Pageable pageable) {
        return postRepository.findAllIndependentPostsByTypeWithMemberDynamic(independentPostType, condition, keyword, pageable);
    }

    @Override
    public Page<Post> findAllRegionPostsByTypesWithMember(RegionType regionType, RegionPostType regionPostType, String condition, String keyword, Pageable pageable) {
        return postRepository.findAllRegionPostsByTypesWithMemberDynamic(regionType, regionPostType, condition, keyword, pageable);
    }

    @Override
    public Page<Post> findAllPostsBySearchWithMember(String condition, String keyword, Pageable pageable) {
        return postRepository.findAllPostsBySearchWithMemberDynamic(condition, keyword, pageable);
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
                        .build()).collect(Collectors.toList());

        myPostServiceDtos.get(0).setTotalCount(findPostPage.getTotalElements());

        return myPostServiceDtos;
    }

    @Override
    public List<MyRecommendPostServiceDto> getMyRecommendPost(Long memberId, int page, int size) {

        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(page, size);
        Page<Post> recommendPostPage = postRepository.findRecommendPostByMemberId(memberId, request);
        List<Post> recommendPosts = recommendPostPage.getContent();

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
                        .build()).collect(Collectors.toList());
        myRecommendPostServiceDtos.get(0).setTotalCount(recommendPostPage.getTotalElements());

        return myRecommendPostServiceDtos;
    }
}
