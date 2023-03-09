package community.independe.repository;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.enums.IndependentPostType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class CommentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void basicCommentTest() {
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        IndependentPost post = IndependentPost.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        postRepository.save(post);

        Comment comment = Comment.builder()
                .content("comment")
                .member(member)
                .post(post)
                .build();
        commentRepository.save(comment);

        Comment findComment = commentRepository.findById(comment.getId()).get();
        Assertions.assertThat(findComment.getContent()).isEqualTo(comment.getContent());
    }
}
