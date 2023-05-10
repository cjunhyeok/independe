package community.independe.repository.post;

import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<Post> findAllRegionPostsByTypesWithMemberDynamic(RegionType regionType,
                                                   RegionPostType regionPostType,
                                                   String condition,
                                                   String keyword,
                                                   Pageable pageable);

    Page<Post> findAllIndependentPostsByTypeWithMemberDynamic(IndependentPostType independentPostType,
                                                       String condition,
                                                       String keyword,
                                                       Pageable pageable);

}
