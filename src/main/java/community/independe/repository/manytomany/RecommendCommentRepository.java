package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendCommentRepository extends JpaRepository<RecommendComment, Long> {

    @Query(value = "select r from RecommendComment r join fetch r.comment" +
            " join fetch r.member" +
            " where r.comment.id = :commentId" +
            " and r.member.id = :memberId",
    countQuery = "select r from RecommendComment r" +
            " where r.comment.id = :commentId" +
            " and r.member.id = :memberId")
    RecommendComment findByCommentIdAndMemberId(@Param("commentId") Long commentId,
                                                @Param("memberId") Long memberId);

    @Query(value = "select count(r) from RecommendComment r" +
            " where r.comment.id = :commentId" +
            " and r.isRecommend = true")
    Long countAllByCommentIdAndIsRecommend(@Param("commentId") Long commentId);

    @Query(value = "select r from RecommendComment r join fetch r.comment rc" +
            " join fetch rc.post" +
            " join fetch r.member" +
            " where r.isRecommend = true" +
            " and r.comment.id = :commentId" +
            " and rc.post.id = :postId" +
            " and r.member.id = :memberId",
    countQuery = "select r from RecommendComment r join fetch r.comment rc" +
            " join fetch rc.post" +
            " join fetch r.member" +
            " where r.isRecommend = true" +
            " and r.comment.id = :commentId" +
            " and rc.post.id = :postId" +
            " and r.member.id = :memberId")
    RecommendComment findByCommentIdAndPostIdAndMemberIdAndIsRecommend(@Param("commentId") Long commentId,
                                                                       @Param("postId") Long postId,
                                                                       @Param("memberId") Long memberId);

    @Query(value = "select r.comment, count(r) as recommendCount from RecommendComment r" +
            " group by r.comment" +
            " having count(r.id) > 5" +
            " order by recommendCount desc")
    List<Object[]> findBestComment();
}
