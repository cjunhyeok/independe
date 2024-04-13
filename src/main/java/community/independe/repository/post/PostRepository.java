package community.independe.repository.post;

import community.independe.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query(value = "select p from Post p" +
            " where p.member.id = :memberId")
    // 작성한 게시글 조회
    Page<Post> findAllByMemberId(@Param("memberId") Long memberId,
                                 Pageable pageable);

    @Query(value = "select rp.post from RecommendPost rp" +
            " where rp.member.id = :memberId" +
            " and rp.isRecommend = true")
    // 추천한 게시글 조회
    Page<Post> findRecommendPostByMemberId(@Param("memberId") Long memberId,
                                           Pageable pageable);

    /**
     * 버전 차이 때문인지 @ManyToOne 관계에서도 fetch join 을 사용하면 페이징 불가
     * select 절에 rp.post 를 사용하면 일반 join 쿼리가 나간다 (fetch join X)
     * rp.member.id --> RecommendPost 테이블의 memberId(FK) 와 비교하므로 member 데이터 필요 X
     */

    /**
     * 페이징을 사용하려면
     * fetch join 없이 조회
     * 연관관계는 default_batch_fetch_size 를 통해 in 쿼리로 해결
     * JPA -> JPA 활용 2 -> API 개발 고급 -> V3.1 페이징 한계 돌파
     */
}
