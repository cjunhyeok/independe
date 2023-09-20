package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class RecommendPostRepositoryTest {

    @Autowired
    private RecommendPostRepository recommendPostRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        RecommendPost recommendPost = RecommendPost.builder()
                .isRecommend(false)
                .member(savedMember)
                .post(savedPost)
                .build();

        // when
        RecommendPost savedRecommendPost = recommendPostRepository.save(recommendPost);

        // then
        assertThat(savedRecommendPost).isEqualTo(recommendPost);
        assertThat(savedRecommendPost.getId()).isEqualTo(recommendPost.getId());
        assertThat(savedRecommendPost.getIsRecommend()).isEqualTo(recommendPost.getIsRecommend());
        assertThat(savedRecommendPost.getPost()).isEqualTo(recommendPost.getPost());
        assertThat(savedRecommendPost.getMember()).isEqualTo(recommendPost.getMember());
        assertThat(savedRecommendPost.getCreatedDate()).isNotNull();
    }

    @Test
    void countAllByPostIdAndIsRecommendTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        RecommendPost recommendPost = RecommendPost.builder()
                .isRecommend(true)
                .member(savedMember)
                .post(savedPost)
                .build();
        recommendPostRepository.save(recommendPost);

        RecommendPost secondRecommendPost = RecommendPost.builder()
                .isRecommend(true)
                .member(savedMember)
                .post(savedPost)
                .build();
        recommendPostRepository.save(secondRecommendPost);

        // when
        Long count = recommendPostRepository.countAllByPostIdAndIsRecommend(savedPost.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void findByPostIdAndMemberIdTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        RecommendPost recommendPost = RecommendPost.builder()
                .isRecommend(true)
                .member(savedMember)
                .post(savedPost)
                .build();
        recommendPostRepository.save(recommendPost);

        // when
        RecommendPost findRecommendPost =
                recommendPostRepository.findByPostIdAndMemberId(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findRecommendPost).isEqualTo(recommendPost);
    }

    @Test
    void findByPostIdAndMemberIdAndIsRecommendTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        RecommendPost recommendPost = RecommendPost.builder()
                .isRecommend(true)
                .member(savedMember)
                .post(savedPost)
                .build();
        recommendPostRepository.save(recommendPost);

        // when
        RecommendPost findRecommendPost =
                recommendPostRepository.findByPostIdAndMemberIdAndIsRecommend(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findRecommendPost).isEqualTo(recommendPost);
    }
}
