package community.independe.repository.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {

    @Query(value = "select f from FavoritePost f join fetch f.post" +
            " join fetch f.member" +
            " where f.post.id = :postId" +
            " and f.member.id = :memberId",
    countQuery = "select f from FavoritePost f" +
            " where f.post.id = :postId" +
            " and f.member.id = :memberId")
    FavoritePost findByPostIdAndMemberId(@Param("postId") Long postId,
                                         @Param("memberId") Long memberId);

    @Query(value = "select f from FavoritePost f join fetch f.post" +
            " join fetch f.member" +
            " where f.isFavorite = true" +
            " and f.post.id = :postId" +
            " and f.member.id = :memberId")
    FavoritePost findByPostIdAndMemberIdAndIsRecommend(@Param("postId") Long postId,
                                                        @Param("memberId") Long memberId);

    @Query(value = "select fp" +
            " from FavoritePost fp join fetch fp.member" +
            " join fetch fp.post" +
            " where fp.member.id = :memberId")
    List<FavoritePost> findByMemberId(@Param("memberId") Long memberId);
}
