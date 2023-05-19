package community.independe.repository.keyword;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.keyword.KeywordDto;
import jakarta.persistence.EntityManager;

import java.util.List;

import static community.independe.domain.keyword.QKeyword.keyword1;


public class KeywordRepositoryCustomImpl implements KeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public KeywordRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<KeywordDto> findKeywordsByGroup() {
        return queryFactory
                .select(Projections.constructor(KeywordDto.class,
                        keyword1.keyword,
                        keyword1.count()))
                .from(keyword1)
                .groupBy(keyword1.keyword)
                .orderBy(keyword1.count().desc())
                .offset(0)
                .limit(10)
                .fetch();
    }
}
