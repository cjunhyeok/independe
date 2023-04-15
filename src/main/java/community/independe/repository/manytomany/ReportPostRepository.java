package community.independe.repository.manytomany;

import community.independe.domain.manytomany.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {

    @Query(value = "select r from ReportPost r join fetch r.post" +
            " join fetch r.member" +
            " where r.post.id = :postId" +
            " and r.member.id = :memberId",
    countQuery = "select r from ReportPost r" +
            " where r.post.id = :postId" +
            " and r.member.id = :memberId")
    ReportPost findByPostIdAndMemberId(@Param("postId") Long postId,
                                       @Param("memberId") Long memberId);

    @Query(value = "select r from ReportPost r join fetch r.post" +
            " join fetch r.member" +
            " where r.isReport = true" +
            " and r.post.id = :postId" +
            " and r.member.id = :memberId",
    countQuery = "select r from ReportPost r" +
            " where r.isReport = true" +
            " and r.post.id = :postId" +
            " and r.member.id = :memberId")
    ReportPost findByPostIdAndMemberIdAndIsRecommend(@Param("postId") Long postId,
                                          @Param("memberId") Long memberId);
}
