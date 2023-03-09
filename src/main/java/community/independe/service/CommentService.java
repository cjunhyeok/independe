package community.independe.service;

import community.independe.domain.comment.Comment;

public interface CommentService {

    Comment findById(Long id);

    Long createParentPost(Long memberId, Long postId, String content);

    Long createChildPost(Long memberId, Long postId, Long commentId, String content);
}
