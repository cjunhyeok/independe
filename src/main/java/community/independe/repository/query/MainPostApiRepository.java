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
public class MainPostApiRepository {

    private final EntityManager em;

    public List<Post> findAllPopularPosts(LocalDateTime yesterday, LocalDateTime today, int first, int max) {
        return em.createQuery("select p from Post p" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                        " order by p.views DESC", Post.class)
                .setFirstResult(first)
                .setMaxResults(max)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .getResultList();
    }

    public List<Post> findAllIndependentPostByRecommendCount(LocalDateTime yesterday, LocalDateTime today, int first, int max) {
        return em.createQuery("select p from Post p left join p.recommendPosts rp" +
                " where p.createdDate between :yesterday and :today" +
                " and p.independentPostType is not null" +
                " group by p.id" +
                " order by count(rp.post) desc, p.views desc", Post.class)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public List<Post> findAllRegionAllPostByRecommendCount(LocalDateTime yesterday, LocalDateTime today, int first, int max) {
        return em.createQuery("select p from Post p left join p.recommendPosts rp" +
                " where p.createdDate between :yesterday and :today" +
                " and p.regionType is not null" +
                " and p.regionPostType is not null" +
                " and p.regionType = :regionType" +
                " group by p.id" +
                " order by count(rp.post) desc, p.views desc", Post.class)
                .setFirstResult(first)
                .setMaxResults(max)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .setParameter("regionType", RegionType.ALL)
                .getResultList();
    }

    public List<Post> findRegionNotAllPostByRecommendCount(LocalDateTime yesterday, LocalDateTime today, int first, int max) {
        return em.createQuery("select p from Post p left join p.recommendPosts rp" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                " and p.independentPostType IS NULL" +
                " and p.regionType <> :regionType" +
                " group by p.id" +
                " order by count(rp.post) desc, p.views desc", Post.class)
                .setFirstResult(first)
                .setMaxResults(max)
                .setParameter("yesterday", yesterday)
                .setParameter("today", today)
                .setParameter("regionType", RegionType.ALL)
                .getResultList();
    }

}