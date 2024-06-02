package community.independe.api.manytomany;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.manytomany.recommendpost.RecommendPostResponse;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.security.service.MemberContext;
import community.independe.service.PostService;
import community.independe.service.dtos.MyRecommendPostServiceDto;
import community.independe.service.manytomany.RecommendPostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendPostApiController {

    private final RecommendPostService recommendPostService;
    private final PostService postService;

    @Operation(summary = "게시글 추천 *")
    @PostMapping("/api/recommendPost/{postId}")
    public Result addRecommendPost(@PathVariable(name = "postId") Long postId,
                                                 @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();

        RecommendPost findRecommendPostByPost = recommendPostService.findByPostIdAndMemberId(postId, loginMemberId);

        if (findRecommendPostByPost == null) {
            recommendPostService.save(postId, loginMemberId);
        } else if(findRecommendPostByPost.getIsRecommend() == false) {
            recommendPostService.updateIsRecommend(findRecommendPostByPost, true);
        } else if(findRecommendPostByPost.getIsRecommend() == true) {
            recommendPostService.updateIsRecommend(findRecommendPostByPost, false);
        }

        Long countRecommendPost = recommendPostService.countAllByPostIdAndIsRecommend(postId);
        RecommendPostResponse recommendPostResponse =
                RecommendPostResponse.builder().recommendPostCount(countRecommendPost).build();

        return new Result(recommendPostResponse);
    }

    @GetMapping("/api/recommendPost")
    @Operation(summary = "게시글 추천 목록 조회 * (마이페이지)")
    public Result getRecommendPost(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                   @AuthenticationPrincipal MemberContext memberContext) {
        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();
        Long totalCount = 0L;

        List<MyRecommendPostServiceDto> response = postService.getMyRecommendPost(loginMemberId, page, size);

        if (!response.isEmpty()) {
            totalCount = response.get(0).getTotalCount();
        }

        return new Result(response, totalCount);
    }
}
