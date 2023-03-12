package community.independe.api;

import community.independe.api.dtos.post.IndependentPostsResponse;
import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.enums.IndependentPostType;
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
public class IndependentPostApiController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/api/posts/independent/{type}")
    public Result independentPosts(@PathVariable(name = "type") IndependentPostType independentPostType,
                                   @PageableDefault(size = 10)Pageable pageable) {

        Page<IndependentPost> allIndependentPosts = postService.findAllIndependentPosts(independentPostType, pageable);
        List<IndependentPost> independentPosts = allIndependentPosts.getContent();
        long totalCount = allIndependentPosts.getTotalElements();

        List<IndependentPostsResponse> collect = independentPosts.stream()
                .map(p -> new IndependentPostsResponse(
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
