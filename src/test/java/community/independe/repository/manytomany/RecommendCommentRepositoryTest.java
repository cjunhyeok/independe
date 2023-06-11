package community.independe.repository.manytomany;

import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class RecommendCommentRepositoryTest {

    @Autowired
    private RecommendCommentRepository recommendCommentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void saveTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedComment = commentRepository.save(comment);

        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(savedMember)
                .comment(savedComment)
                .build();

        // when
        RecommendComment savedRecommendComment = recommendCommentRepository.save(recommendComment);

        // then
        assertThat(savedRecommendComment).isEqualTo(recommendComment);
    }

    @Test
    public void findByCommentIdAndMemberIdTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedComment = commentRepository.save(comment);

        Comment nextComment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedNextComment = commentRepository.save(nextComment);

        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(savedMember)
                .comment(savedNextComment)
                .build();
        RecommendComment savedRecommendComment = recommendCommentRepository.save(recommendComment);

        // when
        RecommendComment findByCommentIdAndMemberId = recommendCommentRepository.findByCommentIdAndMemberId(savedNextComment.getId(), member.getId());

        // then
        assertThat(findByCommentIdAndMemberId).isEqualTo(findByCommentIdAndMemberId);
        assertThat(findByCommentIdAndMemberId.getComment()).isEqualTo(savedNextComment);
    }

    @Test
    public void countAllByCommentIdAndIsRecommend() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedComment = commentRepository.save(comment);

        Comment nextComment = Comment.builder()
                .content("nextContent")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedNextComment = commentRepository.save(nextComment);

        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(savedMember)
                .comment(savedNextComment)
                .build();
        recommendCommentRepository.save(recommendComment);

        for (int i = 0; i < 5; i++) {
            Member iterMember = Member.builder()
                    .username("id" + i)
                    .password("pass" + i)
                    .nickname("nick" + i)
                    .build();
            Member savedIterMember = memberRepository.save(iterMember);

            RecommendComment iterRecommendComment = RecommendComment.builder()
                    .isRecommend(true)
                    .member(savedIterMember)
                    .comment(savedComment)
                    .build();
            RecommendComment savedIterRecommendComment = recommendCommentRepository.save(iterRecommendComment);
        }

        // when
        Long count = recommendCommentRepository.countAllByCommentIdAndIsRecommend(savedComment.getId());

        assertThat(count).isEqualTo(5);
    }

    @Test
    public void findByCommentIdAndPostIdAndMemberIdAndIsRecommendTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedComment = commentRepository.save(comment);

        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(savedMember)
                .comment(savedComment)
                .build();
        RecommendComment savedRecommendComment = recommendCommentRepository.save(recommendComment);

        RecommendComment recommendCommentFalse = RecommendComment.builder()
                .isRecommend(false)
                .member(savedMember)
                .comment(savedComment)
                .build();
        RecommendComment savedRecommendCommentFalse = recommendCommentRepository.save(recommendCommentFalse);

        // when
        RecommendComment findByCommentIdAndPostIdAndMemberIdAndIsRecommend
                = recommendCommentRepository.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(savedComment.getId(), savedPost.getId(), savedMember.getId());

        // then
        assertThat(findByCommentIdAndPostIdAndMemberIdAndIsRecommend).isEqualTo(savedRecommendComment);
    }

    @Test
    public void findBestCommentTest() {
        // todo
    }
}
