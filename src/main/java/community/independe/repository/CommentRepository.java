package community.independe.repository;

import community.independe.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c left join fetch c.parent" +
            " where c.post.id = :postId",
    countQuery = "select c from Comment c" +
            " where c.post = :postId")
    List<Comment> findAllByPostId(@Param("postId") Long PostId);
}
