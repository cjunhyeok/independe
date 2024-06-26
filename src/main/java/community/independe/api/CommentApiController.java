package community.independe.api;

import community.independe.api.dtos.comment.CreateChildCommentRequest;
import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.domain.alarm.AlarmType;
import community.independe.security.service.MemberContext;
import community.independe.service.AlarmService;
import community.independe.service.CommentService;
import community.independe.service.EmitterService;
import community.independe.service.PostService;
import community.independe.service.dtos.FindCommentDto;
import community.independe.service.dtos.post.FindPostDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final static String POST_MESSAGE = "작성한 게시글에 댓글이 작성되었습니다.";
    private final static String COMMENT_MESSAGE = "작성한 댓글에 대댓글이 작성되었습니다.";
    private final CommentService commentService;
    private final EmitterService emitterService;
    private final AlarmService alarmService;
    private final PostService postService;

    @Operation(summary = "부모 댓글 생성 *")
    @PostMapping("/api/comments/parent/new")
    public ResponseEntity<Long> createParentComment(@RequestBody @Valid CreateParentCommentRequest request,
                                                    @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext.getMemberId();

        Long parentComment = commentService.createParentComment(
                loginMemberId,
                request.getPostId(),
                request.getContent());

        FindPostDto findPostDto = postService.findById(request.getPostId());

        emitterService.notify(findPostDto.getMemberId(), POST_MESSAGE);
        alarmService.saveAlarm(POST_MESSAGE, false, AlarmType.POST, findPostDto.getMemberId());

        return ResponseEntity.ok(parentComment);
    }

    @Operation(summary = "자식 댓글 생성 *")
    @PostMapping("/api/comments/child/new")
    public ResponseEntity<Long> createChildComment(@RequestBody @Valid CreateChildCommentRequest request,
                                                   @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext.getMemberId();

        Long childComment = commentService.createChildComment(
                loginMemberId,
                request.getPostId(),
                request.getParentId(),
                request.getContent()
        );

        FindPostDto findPostDto = postService.findById(request.getPostId());
        FindCommentDto findParentCommentDto = commentService.findById(request.getParentId());

        emitterService.notify(findPostDto.getMemberId(), POST_MESSAGE);
        alarmService.saveAlarm(POST_MESSAGE, false, AlarmType.POST, findPostDto.getMemberId());

        emitterService.notify(findParentCommentDto.getMemberId(), COMMENT_MESSAGE);
        alarmService.saveAlarm(COMMENT_MESSAGE, false, AlarmType.COMMENT, findParentCommentDto.getMemberId());

        return ResponseEntity.ok(childComment);
    }
}
