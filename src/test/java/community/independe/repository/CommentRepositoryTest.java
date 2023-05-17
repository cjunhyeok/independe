package community.independe.repository;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.post.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class CommentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    public void saveParentCommentTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
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

        // when
        Comment findComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(findComment.getContent()).isEqualTo(comment.getContent());
    }

    @Test
    public void saveChildCommentTest() {

        // given
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        postRepository.save(post);

        Comment parentComment = Comment.builder()
                .content("parent")
                .member(member)
                .post(post)
                .build();
        Comment savedParent = commentRepository.save(parentComment);

        Comment childComment = Comment.builder()
                .content("child")
                .member(member)
                .post(post)
                .parent(savedParent)
                .build();
        Comment savedChild = commentRepository.save(childComment);

        // when
        Comment findComment = commentRepository.findById(savedChild.getId()).orElseThrow(()
                -> new IllegalArgumentException("Comment not exist"));

        // then
        assertThat(findComment.getParent().getId()).isEqualTo(savedParent.getId());
    }
}
