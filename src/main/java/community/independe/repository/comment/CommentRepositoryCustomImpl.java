package community.independe.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public CommentRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public int deleteCommentsByPostId(Long postId) {
        return em.createQuery("delete from Comment c where c.post.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }

    @Override
    public int deleteCommentByParentId(Long commentId) {
        return em.createQuery("delete from Comment c where c.parent.id = :parentId")
                .setParameter("parentId", commentId)
                .executeUpdate();
    }


}
