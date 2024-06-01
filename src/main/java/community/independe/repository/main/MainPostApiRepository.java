package community.independe.repository.main;

import community.independe.domain.post.Post;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.main.MainPostPageRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MainPostApiRepository {

    private final EntityManager em;

    public List<Post> findAllPopularPosts(MainPostPageRequest request) {
        return em.createQuery("select p from Post p" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                        " order by p.views DESC", Post.class)
                .setFirstResult(request.getOffset())
                .setMaxResults(request.getLimit())
                .setParameter("yesterday", request.getDateOffset())
                .setParameter("today", request.getDateLimit())
                .getResultList();
    }

    public List<Post> findAllIndependentPostByRecommendCount(MainPostPageRequest request) {
        return em.createQuery("select p from Post p left join p.recommendPosts rp" +
                " where p.createdDate between :yesterday and :today" +
                " and p.independentPostType is not null" +
                " group by p.id" +
                " order by count(rp.post) desc, p.views desc", Post.class)
                .setFirstResult(request.getOffset())
                .setMaxResults(request.getLimit())
                .setParameter("yesterday", request.getDateOffset())
                .setParameter("today", request.getDateLimit())
                .getResultList();
    }

    public List<Post> findAllRegionAllPostByRecommendCount(MainPostPageRequest request) {
        return em.createQuery("select p from Post p left join p.recommendPosts rp" +
                " where p.createdDate between :yesterday and :today" +
                " and p.regionType = :regionType" +
                " group by p.id" +
                " order by count(rp.post) desc, p.views desc", Post.class)
                .setFirstResult(request.getOffset())
                .setMaxResults(request.getLimit())
                .setParameter("yesterday", request.getDateOffset())
                .setParameter("today", request.getDateLimit())
                .setParameter("regionType", RegionType.ALL)
                .getResultList();
    }

    public List<Post> findRegionNotAllPostByRecommendCount(MainPostPageRequest request) {
        return em.createQuery("select p from Post p left join p.recommendPosts rp" +
                " where p.createdDate BETWEEN :yesterday AND :today" +
                " and p.regionType <> :regionType" +
                " group by p.id" +
                " order by count(rp.post) desc, p.views desc", Post.class)
                .setFirstResult(request.getOffset())
                .setMaxResults(request.getLimit())
                .setParameter("yesterday", request.getDateOffset())
                .setParameter("today", request.getDateLimit())
                .setParameter("regionType", RegionType.ALL)
                .getResultList();
    }

}