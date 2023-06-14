package community.independe.repository.file;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

public class FilesRepositoryCustomImpl implements FilesRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public FilesRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public int deleteFilesByPostId(Long postId) {
        return em.createQuery("delete from Files f where f.post.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}
