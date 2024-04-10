package community.independe.repository.comment;

import community.independe.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query(value = "select c from Comment c left join fetch c.parent" +
            " join fetch c.member" +
            " where c.post.id = :postId" +
            " order by c.parent.id ASC nulls first, c.createdDate asc",
    countQuery = "select c from Comment c" +
            " where c.post = :postId")
    List<Comment> findAllByPostId(@Param("postId") Long PostId);

    Long countAllByPostId(Long PostId);

    @Query(value = "select c from Comment c left join fetch c.parent" +
            " join fetch c.member" +
            " join fetch c.post" +
            " where c.member.id = :memberId")
    List<Comment> findAllByMemberId(@Param("memberId") Long memberId);
}
