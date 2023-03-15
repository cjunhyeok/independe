package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.CreateIndependentPostRequest;
import community.independe.api.dtos.post.PostsResponse;
import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.service.CommentService;
import community.independe.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                   @PageableDefault(
                                           size = 10,
                                           sort = "lastModifiedDate",
                                           direction = Sort.Direction.DESC) Pageable pageable) {

        Page<IndependentPost> allIndependentPosts = postService.findAllIndependentPostsByType(independentPostType, pageable);
        List<IndependentPost> independentPosts = allIndependentPosts.getContent();
        long totalCount = allIndependentPosts.getTotalElements();

        List<PostsResponse> collect = independentPosts.stream()
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

    @PostMapping("/api/posts/independent/new")
    public ResponseEntity<Long> createIndependentPost(@RequestBody @Valid CreateIndependentPostRequest request) {

        Long independentPost = postService.createIndependentPost(
                request.getMemberId(),
                request.getTitle(),
                request.getContent(),
                request.getIndependentPostType());

        return ResponseEntity.ok(independentPost);
    }
}
