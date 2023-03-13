package community.independe.service;

import community.independe.domain.comment.Comment;
import community.independe.domain.post.enums.IndependentPostType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback(value = false)
public class CommentServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    @Test
    public void createParentCommentTest() {

        Long joinMemberId = memberService.join("id1", "1234", "nickname", null, null, null, null, null);

        String title = "title";
        String content = "content";
        IndependentPostType independentPostType = IndependentPostType.COOK;

        Long independentPostId = postService.createIndependentPost(joinMemberId, title, content, independentPostType);

        Long parentId = commentService.createParentComment(joinMemberId, independentPostId, "parent");

        Comment findComment = commentService.findById(parentId);
        Assertions.assertThat(findComment.getId()).isEqualTo(parentId);
    }

    @Test
    public void createChildCommentTest() {

        Long joinMemberId = memberService.join("id1", "1234", "nickname", null, null, null, null, null);

        String title = "title";
        String content = "content";
        IndependentPostType independentPostType = IndependentPostType.COOK;

        Long independentPostId = postService.createIndependentPost(joinMemberId, title, content, independentPostType);

        Long parentId = commentService.createParentComment(joinMemberId, independentPostId, "parent");

        Long childId = commentService.createChildComment(joinMemberId, independentPostId, parentId, "child");
        Comment childComment = commentService.findById(childId);
        Comment parentComment = commentService.findById(parentId);
        Assertions.assertThat(childComment.getParent().getId()).isEqualTo(parentComment.getId());
    }
}
