package community.independe.service;

import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Post findById(Long postId);

    // 자취 게시판에 글 쓰기
    Long createIndependentPost(Long memberId, String title, String content,
                               IndependentPostType independentPostType);

    // 지역 게시판에 글 쓰기
    Long createRegionPost(Long memberId, String title, String content,
                          RegionType regionType, RegionPostType regionPostType);

    Page<Post> findAllIndependentPostsByTypeWithMember(IndependentPostType independentPostType,
                                                       String condition,
                                                       String keyword,
                                                       Pageable pageable);

    Page<Post> findAllRegionPostsByTypesWithMember(RegionType regionType,
                                                   RegionPostType regionPostType,
                                                   String condition,
                                                   String keyword,
                                                   Pageable pageable);

    Page<Post> findAllPostsBySearchWithMember(String condition, String keyword, Pageable pageable);

    void increaseViews(Long postId);
}
