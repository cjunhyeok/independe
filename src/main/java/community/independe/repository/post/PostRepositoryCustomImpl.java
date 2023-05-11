package community.independe.repository.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static community.independe.domain.member.QMember.member;
import static community.independe.domain.post.QPost.post;

public class PostRepositoryCustomImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> findAllRegionPostsByTypesWithMemberDynamic(RegionType regionType,
                                                                 RegionPostType regionPostType,
                                                                 String condition, String keyword,
                                                                 Pageable pageable) {
        List<Post> posts = queryFactory
                .select(post)
                .from(post).join(post.member, member).fetchJoin()
                .where(post.regionPostType.eq(regionPostType)
                        .and(post.regionType.eq(regionType)
                                .and(judgeCondition(condition, keyword))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(post.regionPostType.eq(regionPostType)
                        .and(post.regionType.eq(regionType)
                                .and(judgeCondition(condition, keyword))))
                .fetchOne();

        return new PageImpl<>(posts, pageable, count);
    }

    @Override
    public Page<Post> findAllIndependentPostsByTypeWithMemberDynamic(IndependentPostType independentPostType, String condition, String keyword, Pageable pageable) {
        List<Post> posts = queryFactory.select(post)
                .from(post).join(post.member, member).fetchJoin()
                .where(post.independentPostType.eq(independentPostType)
                        .and(judgeCondition(condition, keyword)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(post.independentPostType.eq(independentPostType)
                        .and(judgeCondition(condition, keyword)))
                .fetchOne();

        return new PageImpl<>(posts, pageable, count);
    }


    private BooleanExpression judgeCondition(String condition, String keyword) {
        if (condition.equals("title")) {
            return titleEq(keyword);
        } else if (condition.equals("nickname")) {
            return nicknameEq(keyword);
        } else {
            return null;
        }
    }

    private BooleanExpression titleEq(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        } else {
            return post.title.like("%" + keyword + "%");
        }
    }

    private BooleanExpression nicknameEq(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        } else {
            return post.member.nickname.like("%" + keyword + "%");
        }
    }

}
