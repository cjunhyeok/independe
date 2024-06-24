package community.independe.service.manytomany;

import community.independe.service.manytomany.dtos.GetFavoritePostServiceDto;

import java.util.List;

public interface FavoritePostService {

    Long save(Long postId, Long memberId);

    List<GetFavoritePostServiceDto> findFavoritePostByMemberId(Long memberId, int page, int size);
}
