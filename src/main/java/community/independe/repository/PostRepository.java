package community.independe.repository;

import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.Post;
import community.independe.domain.post.RegionPost;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from IndependentPost p")
    List<IndependentPost> findAllIndependentPosts();

    @Query("select p from RegionPost p")
    List<RegionPost> findAllRegionPosts();

    @Query(value = "select p from IndependentPost p join fetch p.member" +
            " where p.independentPostType = :independentPostType",
    countQuery = "select count(p) from IndependentPost  p")
    Page<IndependentPost> findAllIndependentPostsWithMember(@Param("independentPostType") IndependentPostType independentPostType,
                                                            Pageable pageable);

    @Query(value = "select p from RegionPost p join fetch p.member" +
            " where p.regionType = :regionType" +
            " and p.regionPostType = :regionPostType",
    countQuery = "select p from RegionPost p")
    Page<RegionPost> findAllRegionPostsWithMember(@Param("regionType")RegionType regionType,
                                                  @Param("regionPostType")RegionPostType regionPostType,
                                                  Pageable pageable);
}
