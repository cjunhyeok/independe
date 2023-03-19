package community.independe.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.keyword.KeywordDto;
import jakarta.persistence.EntityManager;

import java.util.List;

import static community.independe.domain.keyword.QKeyword.*;

public class KeywordRepositoryCustomImpl implements KeywordRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public KeywordRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<KeywordDto> findKeywordsByGroup() {
        return queryFactory
                .select(Projections.constructor(KeywordDto.class,
                        keyword.keywordName,
                        keyword.count()))
                .from(keyword)
                .groupBy(keyword.keywordName)
                .orderBy(keyword.count().desc())
                .fetch();
    }
}
