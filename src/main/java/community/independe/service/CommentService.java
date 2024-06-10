package community.independe.service;

import community.independe.api.dtos.post.PostCommentResponse;
import community.independe.service.dtos.FindCommentDto;
import community.independe.service.dtos.MyCommentServiceDto;
import community.independe.service.dtos.MyRecommendCommentServiceDto;

import java.util.List;

public interface CommentService {

    FindCommentDto findById(Long id);

    Long createParentComment(Long memberId, Long postId, String content);

    Long createChildComment(Long memberId, Long postId, Long commentId, String content);

    Long countAllByPostId(Long postId);

    List<PostCommentResponse> findCommentsByPostId(Long postId, Long loginMemberId);

    List<MyCommentServiceDto> getMyComment(Long memberId, int page, int size);

    List<MyRecommendCommentServiceDto> getMyRecommendComment(Long memberId, int page, int size);
}
