package community.independe.api.manytomany;

import community.independe.api.dtos.Result;
import community.independe.security.service.MemberContext;
import community.independe.service.CommentService;
import community.independe.service.dtos.MyRecommendCommentServiceDto;
import community.independe.service.manytomany.RecommendCommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendCommentApiController {

    private final RecommendCommentService recommendCommentService;
    private final CommentService commentService;

    @Operation(summary = "댓글 추천 *")
    @PostMapping("/api/recommendComment/{commentId}")
    public ResponseEntity addRecommendComment(@PathVariable(name = "commentId") Long commentId,
                                              @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();

        Long savedRecommendComment = recommendCommentService.save(commentId, loginMemberId);

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/recommendComment")
    @Operation(summary = "댓글 추천 목록 조회 * (마이페이지)")
    public Result getRecommendComment(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                      @AuthenticationPrincipal MemberContext memberContext) {
        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();
        Long totalCount = 0L;

        List<MyRecommendCommentServiceDto> response = commentService.getMyRecommendComment(loginMemberId, page, size);

        if (!response.isEmpty()) {
            totalCount = response.get(0).getTotalCount();
        }

        return new Result(response, totalCount);
    }
}
