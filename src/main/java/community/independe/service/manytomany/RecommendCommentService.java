package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendComment;

public interface RecommendCommentService {

    Long save(Long commentId, Long memberId);

    RecommendComment findByCommentIdAndMemberId(Long commentId, Long memberId);
}
