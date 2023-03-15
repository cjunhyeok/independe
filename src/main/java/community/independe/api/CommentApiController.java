package community.independe.api;

import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
}
