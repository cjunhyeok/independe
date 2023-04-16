package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendComment;

import java.util.List;

public interface RecommendCommentService {

    Long save(Long commentId, Long memberId);

    RecommendComment findByCommentIdAndMemberId(Long commentId, Long memberId);

    void updateIsRecommend(RecommendComment recommendComment, Boolean isRecommend);

    Long countAllByCommentIdAndIsRecommend(Long commentId);

    RecommendComment findByCommentIdAndPostIdAndMemberIdAndIsRecommend(Long commentId, Long postId, Long memberId);

    List<Object[]> findBestComment();
}
