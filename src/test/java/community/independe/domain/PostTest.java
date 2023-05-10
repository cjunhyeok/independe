package community.independe.domain;

import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PostTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    @Rollback(value = false)
    public void simplePostTest() {

        //given
        Member member = Member.builder()
                .username("member1")
                .password("123")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        String title = "title";
        String content = "content";
        String code = "region";
        Post post = null;

        if (code.equals("independent")) {
            // 요리 카테고리에 글 작성
            IndependentPostType cook = IndependentPostType.COOK;

            post = Post.builder()
                    .title(title + "cook")
                    .content(content + "cook")
                    .independentPostType(cook)
                    .member(member)
                    .build();

            postRepository.save(post);

        } else if (code.equals("region")) {
            // 부산지역
            RegionType pusan = RegionType.PUSAN;
            // 식당 카테고리에 글 작성
            RegionPostType restaurant = RegionPostType.RESTAURANT;

            post = Post.builder()
                    .title(title + "pusan" + "restaurant")
                    .content(content + "pusan" + "restaurant")
                    .regionType(pusan)
                    .regionPostType(restaurant)
                    .member(member)
                    .build();

            postRepository.save(post);
        }

        Post findPost = postRepository.findById(post.getId()).orElseThrow(() -> new IllegalArgumentException());
        RegionPostType regionPostType = findPost.getRegionPostType();

        Assertions.assertThat(findPost).isEqualTo(post);
        Assertions.assertThat(regionPostType).isEqualTo(RegionPostType.RESTAURANT);
    }
}
