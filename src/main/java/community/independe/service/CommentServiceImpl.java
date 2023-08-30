package community.independe.service;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.notfound.CommentNotFountException;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not exist"));
    }

    @Transactional
    @Override
    public Long createParentComment(Long memberId, Long postId, String content) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFountException("Member Not Exist"));

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFountException("Post Not Exist"));

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
                .orElseThrow(() -> new MemberNotFountException("Member Not Exist"));

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFountException("Post Not Exist"));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFountException("Comment Not Exist"));

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
    public List<Comment> findAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
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
            throw new IllegalArgumentException("Region Not Authenticate");
        }
    }
}
