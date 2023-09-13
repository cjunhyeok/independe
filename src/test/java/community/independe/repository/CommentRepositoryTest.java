package community.independe.repository;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.post.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @BeforeEach
    public void initData() {
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post independentPost = Post.builder()
                .title("independentTitle")
                .content("independentContent")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        postRepository.save(independentPost);

        Post regionPost = Post.builder()
                .title("regionTitle")
                .content("regionContent")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .build();
        postRepository.save(regionPost);

        Comment parentComment = Comment.builder()
                .content("parent")
                .member(member)
                .post(regionPost)
                .build();
        Comment savedParent = commentRepository.save(parentComment);

        Comment childComment = Comment.builder()
                .content("child")
                .member(member)
                .post(regionPost)
                .parent(savedParent)
                .build();
        Comment savedChild = commentRepository.save(childComment);
    }

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
        assertThat(savedParent.getChild().get(0).getId()).isEqualTo(findComment.getId());
    }

    @Test
    public void findAllByPostIdWithCountTest() {
        // given
        Post findPost = postRepository.findAll().get(0);
        Member findMember = memberRepository.findAll().get(0);
        Post secondPost = postRepository.findAll().get(1);
        for(int i = 0; i < 5; i++) {
            Comment comment = Comment.builder()
                    .content("content" + i)
                    .post(findPost)
                    .member(findMember)
                    .build();
            commentRepository.save(comment);

            for (int j = 0; j < 3; j++) {
                Comment child = Comment.builder()
                        .content("child")
                        .post(findPost)
                        .member(findMember)
                        .parent(comment)
                        .build();
                commentRepository.save(child);
            }
        }
        Comment secondComment = Comment.builder()
                .content("content")
                .post(secondPost)
                .member(findMember)
                .build();
        commentRepository.save(secondComment);

        // when
        List<Comment> findAllByPostId = commentRepository.findAllByPostId(findPost.getId());
        Long count = commentRepository.countAllByPostId(findPost.getId());

        // then
        assertThat(findAllByPostId.size()).isEqualTo(20);
        assertThat(String.valueOf(findAllByPostId.size())).isEqualTo(count.toString());
        assertThat(findAllByPostId.get(2).getContent()).isEqualTo("content2");
        assertThat(findAllByPostId.get(3).getChild().size()).isEqualTo(3);
        assertThat(findAllByPostId.get(4).getParent()).isNull();
        assertThat(findAllByPostId.get(5).getParent()).isNotNull();
    }

    @Test
    void deleteCommentsByPostIdTest() {
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

        em.flush();
        em.clear();

        // when
        commentRepository.deleteCommentsByPostId(savedParent.getPost().getId());

        // then
        assertThatThrownBy(() -> commentRepository.findById(savedParent.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteParentComment() {
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

        em.flush();
        em.clear();

        // when
        commentRepository.deleteCommentByParentId(savedChild.getParent().getId());

        // then
        assertThatThrownBy(() -> commentRepository.findById(savedChild.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }
}
