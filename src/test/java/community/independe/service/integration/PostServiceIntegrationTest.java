package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.PostService;
import community.independe.service.dtos.MyPostServiceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class PostServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("내가 작성한 게시글을 조회한다.")
    void findMyPostTest() {
        // given
        Member member = Member.builder().nickname("nickname").build();
        Member savedMember = memberRepository.save(member);

        Post independentPost = Post.builder()
                .title("independentTitle")
                .content("independentContent")
                .independentPostType(IndependentPostType.CLEAN)
                .member(savedMember)
                .build();
        Post savedIndependentPost = postRepository.save(independentPost);

        Post regionPost = Post.builder()
                .title("regionTitle")
                .content("regionContent")
                .regionType(RegionType.SEOUL)
                .regionPostType(RegionPostType.RESTAURANT)
                .member(savedMember)
                .build();
        Post savedRegionPost = postRepository.save(regionPost);

        // when
        List<MyPostServiceDto> myPost = postService.findMyPost(savedMember.getId(), 0, 10);

        // then
        assertThat(myPost).hasSize(2);
        assertThat(myPost.get(0).getTotalCount()).isEqualTo(2);
        assertThat(myPost.get(0).getNickname()).isNotNull();
    }
}
