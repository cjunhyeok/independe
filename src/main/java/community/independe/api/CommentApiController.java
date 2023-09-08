package community.independe.api;

import community.independe.api.dtos.comment.CreateChildCommentRequest;
import community.independe.api.dtos.comment.CreateParentCommentRequest;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.security.service.MemberContext;
import community.independe.service.AlarmService;
import community.independe.service.CommentService;
import community.independe.service.EmitterService;
import community.independe.service.PostService;
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

    @PostMapping("/api/comments/parent/new")
    public ResponseEntity<Long> createParentComment(@RequestBody @Valid CreateParentCommentRequest request,
                                                    @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        Long parentComment = commentService.createParentComment(
                loginMember.getId(),
                request.getPostId(),
                request.getContent());

        Post findPost = postService.findById(request.getPostId());

        emitterService.notify(findPost.getMember().getId(), POST_MESSAGE);
        alarmService.saveAlarm(POST_MESSAGE, false, AlarmType.POST, findPost.getMember().getId());

        return ResponseEntity.ok(parentComment);
    }

    @PostMapping("/api/comments/child/new")
    public ResponseEntity<Long> createChildComment(@RequestBody @Valid CreateChildCommentRequest request,
                                                   @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        Long childComment = commentService.createChildComment(
                loginMember.getId(),
                request.getPostId(),
                request.getParentId(),
                request.getContent()
        );

        Post findPost = postService.findById(request.getPostId());
        Comment findParentComment = commentService.findById(request.getParentId());

        emitterService.notify(findPost.getMember().getId(), POST_MESSAGE);
        alarmService.saveAlarm(POST_MESSAGE, false, AlarmType.POST, findPost.getMember().getId());

        emitterService.notify(findParentComment.getMember().getId(), COMMENT_MESSAGE);
        alarmService.saveAlarm(COMMENT_MESSAGE, false, AlarmType.COMMENT, findParentComment.getMember().getId());

        return ResponseEntity.ok(childComment);
    }
}
