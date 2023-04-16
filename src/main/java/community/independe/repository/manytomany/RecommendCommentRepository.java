package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
