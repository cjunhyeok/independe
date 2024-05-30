package community.independe.service;

import community.independe.api.dtos.post.PostsResponse;
import community.independe.api.dtos.post.SearchResponse;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.MyPostServiceDto;
import community.independe.service.dtos.MyRecommendPostServiceDto;
import community.independe.service.dtos.post.FindAllPostsDto;
import community.independe.service.dtos.post.FindIndependentPostsDto;
import community.independe.service.dtos.post.FindRegionPostsDto;

import java.util.List;

public interface PostService {

    Post findById(Long postId);

    // 자취 게시판에 글 쓰기
    Long createIndependentPost(Long memberId, String title, String content,
                               IndependentPostType independentPostType);

    // 지역 게시판에 글 쓰기
    Long createRegionPost(Long memberId, String title, String content,
                          RegionType regionType, RegionPostType regionPostType);

    // 게시글 수정
    Long updatePost(Long postId, String title, String content);

    // 게시글 삭제
    void deletePost(Long postId);

    List<PostsResponse> findIndependentPosts(FindIndependentPostsDto findIndependentPostsDto);

    List<PostsResponse> findRegionPosts(FindRegionPostsDto findRegionPostsDto);

    List<SearchResponse> findAllPosts(FindAllPostsDto findAllPostsDto);

    void increaseViews(Long postId);

    List<MyPostServiceDto> findMyPost(Long memberId, int page, int size);

    List<MyRecommendPostServiceDto> getMyRecommendPost(Long memberId, int page, int size);
}
