package community.independe.service.manytomany;

public interface RecommendPostService {

    Long save(Long postId, Long memberId);

    Long countByPostId(Long postId);
}
