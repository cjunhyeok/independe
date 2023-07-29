package community.independe.repository.post;

import community.independe.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("select p from Post p" +
            " where p.independentPostType IS NOT NULL")
    List<Post> findAllIndependentPosts();

    @Query("select p from Post p" +
            " where p.regionType IS NOT NULL" +
            " and p.regionPostType IS NOT NULL")
    List<Post> findAllRegionPosts();
}
