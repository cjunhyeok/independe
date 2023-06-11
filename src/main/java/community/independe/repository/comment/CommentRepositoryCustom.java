package community.independe.repository.comment;

public interface CommentRepositoryCustom {

    int deleteCommentsByPostId(Long postId);

    int deleteParentComment(Long commentId);
}
