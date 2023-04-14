package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendPostRepository extends JpaRepository<RecommendPost, Long> {

    @Query("select count(r) from RecommendPost r" +
            " where r.post.id = :postId" +
            " and r.isRecommend = true")
    Long countAllByPostIdAndIsRecommend(@Param("postId") Long postId);

    @Query(value = "select r from RecommendPost  r" +
            " where r.post.id = :postId" +
            " and r.member.id = :memberId",
    countQuery = "select r from RecommendPost  r" +
            " where r.post.id = :postId" +
            " and r.member.id = :memberId")
    RecommendPost findByPostIdAndMemberId(@Param("postId") Long postId,
                                          @Param("memberId") Long memberId);
}
