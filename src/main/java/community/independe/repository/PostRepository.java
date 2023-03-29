package community.independe.repository;

import community.independe.domain.post.Post;
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

    @Query("select p from Post p" +
            " where p.independentPostType IS NOT NULL")
    List<Post> findAllIndependentPosts();

    @Query("select p from Post p" +
            " where p.regionType IS NOT NULL" +
            " and p.regionPostType IS NOT NULL")
    List<Post> findAllRegionPosts();

    @Query(value = "select p from Post p join fetch p.member",
    countQuery = "select count(p) from Post p")
    Page<Post> findAllPostsWithMember(Pageable pageable);

    @Query(value = "select p from Post p join fetch p.member" +
            " where p.independentPostType IS NOT NULL",
    countQuery = "select count(p) from Post p" +
            " where p.independentPostType IS NOT NULL")
    Page<Post> findAllIndependentPostsWithMember(Pageable pageable);

    @Query(value = "select p from Post p join fetch p.member" +
            " where p.regionType IS NOT NULL" +
            " and p.regionPostType IS NOT NULL",
    countQuery = "select count(p) from Post p" +
            " where p.regionType IS NOT NULL" +
            " and p.regionPostType IS NOT NULL")
    Page<Post> findAllRegionPostsWithMember(Pageable pageable);

    @Query(value = "select p from Post p join fetch p.member" +
            " where p.independentPostType = :independentPostType",
    countQuery = "select count(p) from Post p" +
            " where p.independentPostType =: independentPostType")
    Page<Post> findAllIndependentPostsByTypeWithMember(@Param("independentPostType") IndependentPostType independentPostType,
                                                       Pageable pageable);

    @Query(value = "select p from Post p join fetch p.member" +
            " where p.regionType = :regionType" +
            " and p.regionPostType = :regionPostType",
    countQuery = "select count(p) from Post p" +
            " where p.regionType = :regionType" +
            " and p.regionPostType = :regionPostType")
    Page<Post> findAllRegionPostsByTypesWithMember(@Param("regionType")RegionType regionType,
                                                   @Param("regionPostType")RegionPostType regionPostType,
                                                   Pageable pageable);
}
