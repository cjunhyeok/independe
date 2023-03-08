package community.independe.repository;

import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.Post;
import community.independe.domain.post.RegionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from IndependentPost p")
    List<IndependentPost> findAllIndependentPosts();

    @Query("select p from RegionPost p")
    List<RegionPost> findAllRegionPosts();
}
