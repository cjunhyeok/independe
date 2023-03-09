package community.independe.service;

public interface CommentService {

    Long createParentPost(Long memberId, Long postId, String content);
}
