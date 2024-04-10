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
import community.independe.service.dtos.MyPostServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

        Post findPost = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        findPost.updatePost(title, content);

        return findPost.getId();
    }

    @Override
    public void deletePost(Long postId) {
        Post findPost = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        findPost.deleteMember();
        findPost.deleteRecommendPosts();

        List<Comment> allByPostId = commentRepository.findAllByPostId(findPost.getId());

        for (Comment comment : allByPostId) {
            commentRepository.deleteCommentByParentId(comment.getId());
        }
        filesRepository.deleteFilesByPostId(findPost.getId());

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
    public List<MyPostServiceDto> findMyPost(Long memberId) {

        List<Post> findPosts = postRepository.findAllByMemberId(memberId);
        return findPosts.stream()
                .map(fp -> MyPostServiceDto.builder()
                        .postId(fp.getId())
                        .title(fp.getTitle())
                        .independentPostType(fp.getIndependentPostType())
                        .regionType(fp.getRegionType())
                        .regionPostType(fp.getRegionPostType())
                        .createdDate(fp.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }
}
