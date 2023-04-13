package community.independe.service;

import community.independe.domain.comment.Comment;

import java.util.List;

public interface CommentService {

    Comment findById(Long id);

    Long createParentComment(Long memberId, Long postId, String content);

    Long createChildComment(Long memberId, Long postId, Long commentId, String content);

    Long countAllByPostId(Long postId);

    List<Comment> findAllByPostId(Long postId);

    void increaseOrDecreaseRecommendCount(Long commentId ,boolean isUp);
}
