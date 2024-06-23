package community.independe.service.manytomany;

import community.independe.api.dtos.post.BestCommentDto;

public interface RecommendCommentService {

    Long save(Long commentId, Long memberId);

    BestCommentDto findBestComment();
}
