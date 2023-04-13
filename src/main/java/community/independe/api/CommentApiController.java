package community.independe.api;

import community.independe.api.dtos.IsUpDto;
import community.independe.api.dtos.comment.CreateChildCommentRequest;
import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/api/comments/parent/new")
    public ResponseEntity<Long> createParentComment(@RequestBody @Valid CreateParentCommentRequest request) {
        Long parentComment = commentService.createParentComment(
                request.getMemberId(),
                request.getPostId(),
                request.getContent());

        return ResponseEntity.ok(parentComment);
    }

    @PostMapping("/api/comments/child/new")
    public ResponseEntity<Long> createChildComment(@RequestBody @Valid CreateChildCommentRequest request) {

        Long childComment = commentService.createChildComment(
                request.getMemberId(),
                request.getPostId(),
                request.getParentId(),
                request.getContent()
        );

        return ResponseEntity.ok(childComment);
    }

    @Operation(summary = "추천수 증감")
    @PostMapping("/api/comments/recommend/{commentId}")
    public ResponseEntity increaseOrDecreaseRecommendCount(@PathVariable(name = "commentId") Long commentId,
                                                           @RequestBody IsUpDto isUp) {
        commentService.increaseOrDecreaseRecommendCount(commentId, isUp);

        return ResponseEntity.ok("OK");
    }
}
