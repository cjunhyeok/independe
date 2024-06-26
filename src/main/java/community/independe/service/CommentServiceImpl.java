package community.independe.service;

import community.independe.api.dtos.post.PostCommentResponse;
import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.util.PageRequestCreator;
import community.independe.service.dtos.FindCommentDto;
import community.independe.service.dtos.MyCommentServiceDto;
import community.independe.service.dtos.MyRecommendCommentServiceDto;
import community.independe.service.util.ActionStatusChecker;
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
public class CommentServiceImpl implements CommentService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RecommendCommentRepository recommendCommentRepository;
    private final ActionStatusChecker actionStatusChecker;

    @Override
    public FindCommentDto findById(Long id) {
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        return FindCommentDto
                .builder()
                .id(findComment.getId())
                .content(findComment.getContent())
                .parentId(findComment.getParent() == null ? null : findComment.getParent().getId())
                .memberId(findComment.getMember().getId())
                .createdDate(findComment.getCreatedDate())
                .build();
    }

    @Transactional
    @Override
    public Long createParentComment(Long memberId, Long postId, String content) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!findPost.getMember().equals(findMember)) {
            checkRegion(findMember, findPost);
        }

        Comment comment = Comment.builder()
                .content(content)
                .member(findMember)
                .post(findPost)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    @Transactional
    @Override
    public Long createChildComment(Long memberId, Long postId, Long commentId, String content) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!findPost.getMember().equals(findMember)) {
            checkRegion(findMember, findPost);
        }

        Comment comment = Comment.builder()
                .content(content)
                .member(findMember)
                .post(findPost)
                .parent(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    @Override
    public Long countAllByPostId(Long postId) {
        return commentRepository.countAllByPostId(postId);
    }

    @Override
    public List<PostCommentResponse> findCommentsByPostId(Long postId, Long loginMemberId) {
        List<Comment> findComments = commentRepository.findAllByPostId(postId);

        return findComments.stream()
                .map(c -> new PostCommentResponse(
                        c.getId(),
                        c.getMember().getNickname(),
                        c.getContent(),
                        c.getCreatedDate(),
                        recommendCommentRepository.countAllByCommentIdAndIsRecommend(c.getId()),
                        (c.getParent() == null) ? null : c.getParent().getId(),
                        c.getMember().getId(),
                        actionStatusChecker.isRecommendComment(c.getId(), postId, loginMemberId)
                )).collect(Collectors.toList());
    }

    private void checkRegion(Member findMember, Post findPost) {

        Boolean isRegionPost = false;

        if (findPost.getIndependentPostType() != null) {
            isRegionPost = false;
        } else if (findPost.getRegionType() == RegionType.ALL){
            isRegionPost = false;
        } else {
            isRegionPost = true;
        }

        if (isRegionPost == true && (findMember.getRegion() == null || !findMember.getRegion().equals(findPost.getRegionType()))) {
            throw new CustomException(ErrorCode.REGION_NOT_AUTHENTICATE);
        }
    }

    @Override
    public List<MyCommentServiceDto> getMyComment(Long memberId, int page, int size) {

        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(page, size);
        Page<Comment> findCommentsPage = commentRepository.findAllByMemberId(memberId, request);
        List<Comment> findComments = findCommentsPage.getContent();
        long totalCount = findCommentsPage.getTotalElements();

        List<MyCommentServiceDto> myCommentServiceDtos = findComments.stream()
                .map(fc -> MyCommentServiceDto.builder()
                        .commentId(fc.getId())
                        .postId(fc.getPost().getId())
                        .content(fc.getContent())
                        .createdDate(fc.getCreatedDate())
                        .totalCount(totalCount)
                        .build()).collect(Collectors.toList());

        return myCommentServiceDtos;
    }

    @Override
    public List<MyRecommendCommentServiceDto> getMyRecommendComment(Long memberId, int page, int size) {
        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(page, size);
        Page<Comment> findRecommendCommentsPage = commentRepository.findRecommendCommentByMemberId(memberId, request);
        List<Comment> findRecommendComments = findRecommendCommentsPage.getContent();
        long totalCount = findRecommendCommentsPage.getTotalElements();

        List<MyRecommendCommentServiceDto> myCommentServiceDtos = findRecommendComments.stream()
                .map(frc -> MyRecommendCommentServiceDto.builder()
                        .commentId(frc.getId())
                        .postId(frc.getPost().getId())
                        .content(frc.getContent())
                        .createdDate(frc.getCreatedDate())
                        .totalCount(totalCount)
                        .build()).collect(Collectors.toList());

        return myCommentServiceDtos;
    }
}
