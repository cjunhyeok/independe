package community.independe.api.manytomany;

import community.independe.domain.member.Member;
import community.independe.service.manytomany.FavoritePostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // json
@RequiredArgsConstructor
@Slf4j
public class FavoritePostApiController {

    private final FavoritePostService favoritePostService;

    @Operation(summary = "즐겨찾기 추가")
    @PostMapping("/api/favoritePost/{postId}")
    public ResponseEntity<Long> addFavoritePost(@PathVariable(name = "postId") Long postId,
                                          @AuthenticationPrincipal Member member) {

        Long savedFavoritePost = favoritePostService.save(postId, 1L);
        return ResponseEntity.ok(savedFavoritePost);
    }

}
