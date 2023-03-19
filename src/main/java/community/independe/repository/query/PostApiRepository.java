package community.independe.repository.query;

import community.independe.domain.post.Post;
import community.independe.domain.post.enums.RegionType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostApiRepository {

    private final EntityManager em;

    public List<Post> findAllPopularPosts(LocalDateTime yesterday, LocalDateTime today) {
        return em.createQuery("select p from Post p" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                        " order by p.views", Post.class)
                .setFirstResult(0)
                .setMaxResults(9)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .getResultList();
    }

    public List<Post> findAllIndependentPostByRecommendCount(LocalDateTime yesterday, LocalDateTime today) {
        return em.createQuery("select p from Post p" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                " and p.independentPostType IS NOT NULL" +
                " order by p.recommendCount", Post.class)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .setFirstResult(0)
                .setMaxResults(9)
                .getResultList();
    }

    public List<Post> findAllRegionAllPostByRecommendCount(LocalDateTime yesterday, LocalDateTime today) {
        return em.createQuery("select p from Post p" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                " and p.regionType IS NOT NULL" +
                " and p.regionPostType IS NOT NULL" +
                " and p.regionType = :regionType" +
                " order by p.recommendCount", Post.class)
                .setFirstResult(0)
                .setMaxResults(4)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .setParameter("regionType", RegionType.ALL)
                .getResultList();
    }

    public List<Post> findRegionNotAllPostByRecommendCount(LocalDateTime yesterday, LocalDateTime today) {
        return em.createQuery("select p from Post p" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                " and p.independentPostType IS NULL" +
                " and p.regionType <> :regionType" +
                " order by p.recommendCount", Post.class)
                .setFirstResult(0)
                .setMaxResults(4)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .setParameter("regionType", RegionType.ALL)
                .getResultList();
    }

}
