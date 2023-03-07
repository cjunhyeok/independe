package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.IndependentPost;
import community.independe.domain.post.RegionPost;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback(value = false)
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;

    @Test
    public void basicCreateIndependentPostTest() {

        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        Long joinMemberId = memberService.join(member);

        String title = "title";
        String content = "content";
        IndependentPostType independentPostType = IndependentPostType.COOK;

        Long independentPostId = postService.createIndependentPost(joinMemberId, title, content, independentPostType);

        IndependentPost findPost = (IndependentPost)postService.findById(independentPostId);

        Assertions.assertThat(findPost.getIndependentPostType()).isEqualTo(IndependentPostType.COOK);
    }

    @Test
    public void basicCreateRegionPostTest() {

        Member member = Member.builder()
                .username("id1")
                .password("1234")
                .nickname("nickname")
                .role("ROLE_USER")
                .build();

        Long joinMemberId = memberService.join(member);

        String title = "title";
        String content = "content";
        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.RESTAURANT;


        Long regionPostId = postService.createRegionPost(joinMemberId, title, content, regionType, regionPostType);

        RegionPost findPost = (RegionPost)postService.findById(regionPostId);

        Assertions.assertThat(findPost.getRegionPostType()).isEqualTo(RegionPostType.RESTAURANT);
    }
}
