package community.independe.service;

import community.independe.domain.comment.Comment;
import community.independe.service.dtos.MyCommentServiceDto;
import community.independe.service.dtos.MyRecommendCommentServiceDto;

import java.util.List;

public interface CommentService {

    Comment findById(Long id);

    Long createParentComment(Long memberId, Long postId, String content);

    Long createChildComment(Long memberId, Long postId, Long commentId, String content);

    Long countAllByPostId(Long postId);

    List<Comment> findAllByPostId(Long postId);

    List<MyCommentServiceDto> getMyComment(Long memberId, int page, int size);

    List<MyRecommendCommentServiceDto> getMyRecommendComment(Long memberId, int page, int size);
}
