package community.independe.repository;

import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.post.PostRepository;
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
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void basicPostTest() {
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        Post independentPost = Post.builder()
                .title("title2")
                .content("content2")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        Post regionPost = Post.builder()
                .title("title3")
                .content("content3")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.RESTAURANT)
                .build();

        postRepository.save(post);
        postRepository.save(independentPost);
        postRepository.save(regionPost);

        List<Post> findAllPost = postRepository.findAll();

        Assertions.assertThat(findAllPost.size()).isEqualTo(3);
    }

    @Test
    public void findAllChildPostTest() {
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        Post independentPost = Post.builder()
                .title("title2")
                .content("content2")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        Post regionPost = Post.builder()
                .title("title3")
                .content("content3")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.RESTAURANT)
                .build();

        postRepository.save(post);
        postRepository.save(independentPost);
        postRepository.save(regionPost);

        List<Post> allIndependentPost = postRepository.findAllIndependentPosts();
        List<Post> allRegionPost = postRepository.findAllRegionPosts();

        for (Post independentPost1 : allIndependentPost) {
            System.out.println(independentPost1.getIndependentPostType());
        }

        for (Post regionPost1 : allRegionPost) {
            System.out.println(regionPost1.getRegionPostType());
        }

        Assertions.assertThat(allIndependentPost.size()).isEqualTo(2);
        Assertions.assertThat(allRegionPost.size()).isEqualTo(1);
    }

    @Test
    public void basicFetchTest() {
        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post regionPost = Post.builder()
                .title("title3")
                .content("content3")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.RESTAURANT)
                .build();
        postRepository.save(regionPost);

        em.flush();
        em.clear();

        List<Post> allRegionPosts = postRepository.findAllRegionPosts();

        for (Post allRegionPost : allRegionPosts) {
            System.out.println(allRegionPost.getMember().getUsername());
        }

    }

}
