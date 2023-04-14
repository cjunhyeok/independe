package community.independe.service;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.service.manytomany.FavoritePostService;
import community.independe.service.manytomany.RecommendPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class ManyToManyServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FavoritePostService favoritePostService;
    @Autowired
    private RecommendPostService recommendPostService;

    @Test
    public void favoritePostSaveTest() {
        Long joinMemberId = memberService.join("id1", "1234", "nickname", null, null, null, null, null);

        Long independentPostId = postService.createIndependentPost(joinMemberId, "title", "content", IndependentPostType.COOK);

        favoritePostService.save(independentPostId, joinMemberId);
    }

    @Test
    public void recommendPostSaveTest() {
        Long joinMemberId = memberService.join("id1", "1234", "nickname", null, null, null, null, null);

        Long independentPostId = postService.createIndependentPost(joinMemberId, "title", "content", IndependentPostType.COOK);

        recommendPostService.save(independentPostId, joinMemberId);
    }
}
