package community.independe.api.manytomany;

import community.independe.api.dtos.Result;
import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.security.service.MemberContext;
import community.independe.service.manytomany.FavoritePostService;
import community.independe.service.manytomany.dtos.GetFavoritePostServiceDto;
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
public class FavoritePostApiController {

    private final FavoritePostService favoritePostService;

    @Operation(summary = "즐겨찾기 추가 *")
    @PostMapping("/api/favoritePost/{postId}")
    public ResponseEntity addFavoritePost(@PathVariable(name = "postId") Long postId,
                                          @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();
        FavoritePost findFavoritePost = favoritePostService.findByPostIdAndMemberId(postId, loginMember.getId());

        if (findFavoritePost == null) {
            favoritePostService.save(postId, loginMember.getId());
        } else if (findFavoritePost.getIsFavorite() == false) {
            favoritePostService.updateIsFavorite(findFavoritePost, true);
        } else if (findFavoritePost.getIsFavorite() == true) {
            favoritePostService.updateIsFavorite(findFavoritePost, false);
        }
        return ResponseEntity.ok("OK");
    }

    // 즐겨찾기 목록
    @GetMapping("/api/favoritePost")
    @Operation(summary = "게시글 즐겨찾기 목록 조회 * (마이페이지)")
    public Result getFavoritePost(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                  @AuthenticationPrincipal MemberContext memberContext) {
        Member loginMember = memberContext.getMember();

        List<GetFavoritePostServiceDto> response = favoritePostService.findFavoritePostByMemberId(loginMember.getId(), page, size);
        Long totalCount = response.get(0).getTotalCount();

        return new Result<>(response, totalCount);
    }

}
