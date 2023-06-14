package community.independe.repository.video;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.video.Video;
import jakarta.persistence.EntityManager;

import java.util.List;

import static community.independe.domain.video.QVideo.*;


public class VideoRepositoryCustomImpl implements VideoRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public VideoRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Video> findAllForMain() {
        // 각각의 independentPostType에 대한 최대 views값을 찾는 하위쿼리를 생성합니다.
        SubQueryExpression<Integer> subQuery1 = JPAExpressions.select(video.views.max())
                .from(video)
                .where(video.independentPostType.eq(IndependentPostType.COOK));

        SubQueryExpression<Integer> subQuery2 = JPAExpressions.select(video.views.max())
                .from(video)
                .where(video.independentPostType.eq(IndependentPostType.CLEAN));

        SubQueryExpression<Integer> subQuery3 = JPAExpressions.select(video.views.max())
                .from(video)
                .where(video.independentPostType.eq(IndependentPostType.WASH));

        // 각각의 independentPostType에 대한 최대 views값과 일치하는 레코드를 가져오는 쿼리를 생성합니다.
        List<Video> result = queryFactory.selectFrom(video)
                .where(
                        video.independentPostType.eq(IndependentPostType.COOK).and(video.views.eq(subQuery1))
                                .or(video.independentPostType.eq(IndependentPostType.CLEAN).and(video.views.eq(subQuery2)))
                                .or(video.independentPostType.eq(IndependentPostType.WASH).and(video.views.eq(subQuery3)))
                )
                .limit(3)
                .fetch();

        return result;
    }

    @Override
    public List<Video> findAllByIndependentPostType(IndependentPostType independentPostType) {
        return queryFactory.selectFrom(video)
                .where(video.independentPostType.eq(independentPostType))
                .orderBy(video.views.desc())
                .offset(0)
                .limit(3)
                .fetch();

    }
}
