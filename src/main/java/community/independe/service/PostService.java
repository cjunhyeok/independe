package community.independe.service;

import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;

public interface PostService {

    Post findById(Long postId);

    // 자취 게시판에 글 쓰기
    Long createIndependentPost(Long memberId, String title, String content,
                               IndependentPostType independentPostType);

    // 지역 게시판에 글 쓰기
    Long createRegionPost(Long memberId, String title, String content,
                          RegionType regionType, RegionPostType regionPostType);


}
