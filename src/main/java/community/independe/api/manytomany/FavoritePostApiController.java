package community.independe.api.manytomany;

import community.independe.api.dtos.Result;
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

        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();

        Long savedFavoritePostId = favoritePostService.save(postId, loginMemberId);

        return ResponseEntity.ok(savedFavoritePostId);
    }

    // 즐겨찾기 목록
    @GetMapping("/api/favoritePost")
    @Operation(summary = "게시글 즐겨찾기 목록 조회 * (마이페이지)")
    public Result getFavoritePost(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                  @AuthenticationPrincipal MemberContext memberContext) {
        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();
        Long totalCount = 0L;

        List<GetFavoritePostServiceDto> response = favoritePostService.findFavoritePostByMemberId(loginMemberId, page, size);

        if (!response.isEmpty()) {
            totalCount = response.get(0).getTotalCount();
        }

        return new Result<>(response, totalCount);
    }

}
