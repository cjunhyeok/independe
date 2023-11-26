package community.independe.api.manytomany;

import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.manytomany.RecommendCommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendCommentApiController {

    private final RecommendCommentService recommendCommentService;

    @Operation(summary = "댓글 추천 *")
    @PostMapping("/api/recommendComment/{commentId}")
    public ResponseEntity addRecommendComment(@PathVariable(name = "commentId") Long commentId,
                                              @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        RecommendComment findRecommendComment = recommendCommentService.findByCommentIdAndMemberId(
                commentId, loginMember.getId());

        if (findRecommendComment == null) {
            Long savedRecommendComment = recommendCommentService.save(commentId, loginMember.getId());
        } else if (findRecommendComment.getIsRecommend() == false) {
            recommendCommentService.updateIsRecommend(findRecommendComment, true);
        } else if (findRecommendComment.getIsRecommend() == true) {
            recommendCommentService.updateIsRecommend(findRecommendComment, false);
        }

        return ResponseEntity.ok("OK");
    }
}
