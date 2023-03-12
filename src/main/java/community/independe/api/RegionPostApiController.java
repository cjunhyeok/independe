package community.independe.api;

import community.independe.api.dtos.post.PostsResponse;
import community.independe.domain.post.RegionPost;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.CommentService;
import community.independe.service.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegionPostApiController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/api/posts/region/{regionType}/{regionPostType}")
    public Result regionPosts(@PathVariable(name = "regionType")RegionType regionType,
                              @PathVariable(name = "regionPostType")RegionPostType regionPostType,
                              @PageableDefault(size = 10)Pageable pageable) {

        Page<RegionPost> allRegionPosts = postService.findAllRegionPosts(regionType, regionPostType, pageable);
        List<RegionPost> regionPosts = allRegionPosts.getContent();
        long totalCount = allRegionPosts.getTotalElements();

        List<PostsResponse> collect = regionPosts.stream()
                .map(p -> new PostsResponse(
                        p.getMember().getNickname(),
                        p.getTitle(),
                        p.getLastModifiedDate(),
                        p.getViews(),
                        p.getRecommendCount(),
                        commentService.countAllByPostId(p.getId())
                ))
                .collect(Collectors.toList());

        return new Result(collect, totalCount);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Result<T> {
        private T data;
        private long count;

        public Result(T data) {
            this.data = data;
        }
    }
}
