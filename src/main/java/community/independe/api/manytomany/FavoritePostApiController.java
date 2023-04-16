package community.independe.api.manytomany;

import community.independe.domain.manytomany.FavoritePost;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class FavoritePostApiController {

    private final FavoritePostService favoritePostService;

    @Operation(summary = "즐겨찾기 추가")
    @PostMapping("/api/favoritePost/{postId}")
    public ResponseEntity addFavoritePost(@PathVariable(name = "postId") Long postId,
                                          @AuthenticationPrincipal Member member) {

//        FavoritePost findFavoritePost = favoritePostService.findByPostIdAndMemberId(postId, member.getId());
        FavoritePost findFavoritePost = favoritePostService.findByPostIdAndMemberId(postId, 1L);

        if (findFavoritePost == null) {
//            favoritePostService.save(postId, member.getId());
            favoritePostService.save(postId, 1L);
        } else if (findFavoritePost.getIsFavorite() == false) {
            favoritePostService.updateIsFavorite(findFavoritePost, true);
        } else if (findFavoritePost.getIsFavorite() == true) {
            favoritePostService.updateIsFavorite(findFavoritePost, false);
        }
        return ResponseEntity.ok("OK");
    }

}
