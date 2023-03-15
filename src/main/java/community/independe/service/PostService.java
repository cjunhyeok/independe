package community.independe.service;

import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.Post;
import community.independe.domain.post.RegionPost;
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

    // 자취 게시글 전체 조회
    Page<IndependentPost> findAllIndependentPostsByType(IndependentPostType independentPostType, Pageable pageable);

    // 지역 게시글 전체 조회
    Page<RegionPost> findAllRegionPosts(RegionType regionType, RegionPostType regionPostType, Pageable pageable);


}
