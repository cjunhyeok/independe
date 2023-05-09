package community.independe.api;

import community.independe.api.dtos.comment.CreateChildCommentRequest;
import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/api/comments/parent/new")
    public ResponseEntity<Long> createParentComment(@RequestBody @Valid CreateParentCommentRequest request,
                                                    @AuthenticationPrincipal MemberContext memberContext) {
        Long parentComment = commentService.createParentComment(
//                member.getId(),
                1L,
                request.getPostId(),
                request.getContent());

        return ResponseEntity.ok(parentComment);
    }

    @PostMapping("/api/comments/child/new")
    public ResponseEntity<Long> createChildComment(@RequestBody @Valid CreateChildCommentRequest request,
                                                   @AuthenticationPrincipal Member member) {

        Long childComment = commentService.createChildComment(
//                member.getId(),
                1L,
                request.getPostId(),
                request.getParentId(),
                request.getContent()
        );

        return ResponseEntity.ok(childComment);
    }
}
