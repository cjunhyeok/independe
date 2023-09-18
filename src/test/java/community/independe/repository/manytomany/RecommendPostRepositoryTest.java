package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        Assertions.assertThat(savedRecommendPost).isEqualTo(recommendPost);
    }
}
