package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.post.PostResponse;
import community.independe.domain.comment.Comment;
import community.independe.domain.post.Post;
import community.independe.service.CommentService;
import community.independe.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/api/posts/{postId}")
    public Result post(@PathVariable(name = "postId") Long postId) {

        Post findPost = postService.findById(postId);
        List<Comment> findComments = commentService.findAllByPostId(postId);

        PostResponse postResponse = new PostResponse(findPost, findComments);
        return new Result(postResponse);
    }
}
