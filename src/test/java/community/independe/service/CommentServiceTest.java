package community.independe.service;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
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
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();
        Long joinMemberId = memberService.join(member);

        String title = "title";
        String content = "content";
        IndependentPostType independentPostType = IndependentPostType.COOK;

        Long independentPostId = postService.createIndependentPost(joinMemberId, title, content, independentPostType);

        Long parentId = commentService.createParentPost(joinMemberId, independentPostId, "parent");

        Comment findComment = commentService.findById(parentId);
        Assertions.assertThat(findComment.getId()).isEqualTo(parentId);
    }
}
