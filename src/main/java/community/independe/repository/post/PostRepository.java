package community.independe.repository.post;

import community.independe.domain.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query(value = "select p from Post p" +
            " where p.member.id = :memberId")
    List<Post> findAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
