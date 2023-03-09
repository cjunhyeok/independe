package community.independe.repository;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.RegionPost;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @PersistenceContext
    private EntityManager em;

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

    @Test
    public void findByPostIdTest() {
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        RegionPost regionPost = RegionPost.builder()
                .title("regionTitle")
                .content("regionContent")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.RESTAURANT)
                .build();
        postRepository.save(regionPost);

        IndependentPost independentPost = IndependentPost.builder()
                .title("independentTitle")
                .content("independentContent")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        postRepository.save(independentPost);

        Comment comment1 = Comment.builder()
                .content("comment1")
                .member(member)
                .post(independentPost)
                .build();
        commentRepository.save(comment1);

        Comment comment2 = Comment.builder()
                .content("comment2")
                .member(member)
                .post(independentPost)
                .build();
        commentRepository.save(comment2);

        Comment comment3 = Comment.builder()
                .content("comment3")
                .member(member)
                .post(regionPost)
                .build();
        commentRepository.save(comment3);

        Comment comment4 = Comment.builder()
                .content("comment4")
                .member(member)
                .post(independentPost)
                .parent(comment2)
                .build();
        commentRepository.save(comment4);

        List<Comment> allByPostId = commentRepository.findAllByPostId(independentPost.getId());

        em.flush();
        em.clear();

        for (Comment comment : allByPostId) {
            if (comment.getParent() == null) {
                continue;
            }
            System.out.println(comment.getParent().getContent());
        }
        Assertions.assertThat(allByPostId.size()).isEqualTo(3);
    }
}
