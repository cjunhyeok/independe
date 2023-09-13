package community.independe.repository.comment;

public interface CommentRepositoryCustom {

    int deleteCommentsByPostId(Long postId);

    int deleteCommentByParentId(Long commentId);
}
