package community.independe.repository;

import community.independe.domain.member.Member;
import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.Post;
import community.independe.domain.post.RegionPost;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
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

    @Test
    public void basicPostTest() {
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

        IndependentPost independentPost = IndependentPost.builder()
                .title("title2")
                .content("content2")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        RegionPost regionPost = RegionPost.builder()
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
        for (Post post1 : findAllPost) {
            if (post1 instanceof IndependentPost) {
                System.out.println(((IndependentPost) post1).getIndependentPostType());
            } else if (post1 instanceof RegionPost) {
                System.out.println(((RegionPost) post1).getRegionPostType());
            }
        }
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

        IndependentPost post = IndependentPost.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        IndependentPost independentPost = IndependentPost.builder()
                .title("title2")
                .content("content2")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();

        RegionPost regionPost = RegionPost.builder()
                .title("title3")
                .content("content3")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.RESTAURANT)
                .build();

        postRepository.save(post);
        postRepository.save(independentPost);
        postRepository.save(regionPost);

        List<IndependentPost> allIndependentPost = postRepository.findAllIndependentPost();
        List<RegionPost> allRegionPost = postRepository.findAllRegionPost();

        for (IndependentPost independentPost1 : allIndependentPost) {
            System.out.println(independentPost1.getIndependentPostType());
        }

        for (RegionPost regionPost1 : allRegionPost) {
            System.out.println(regionPost1.getRegionPostType());
        }

        Assertions.assertThat(allIndependentPost.size()).isEqualTo(2);
        Assertions.assertThat(allRegionPost.size()).isEqualTo(1);
    }

}
