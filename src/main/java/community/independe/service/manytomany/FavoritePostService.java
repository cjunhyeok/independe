package community.independe.service.manytomany;

public interface FavoritePostService {

    Long save(Long postId, Long memberId);
}
