package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendPost;

public interface RecommendPostService {

    Long save(Long postId, Long memberId);

    RecommendPost findById(Long recommendPostId);
}
