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
}
