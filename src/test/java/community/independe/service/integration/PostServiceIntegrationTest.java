package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.PostService;
import community.independe.service.dtos.MyPostServiceDto;
import community.independe.service.dtos.MyRecommendPostServiceDto;
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
    @Autowired
    private RecommendPostRepository recommendPostRepository;

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

    @Test
    @DisplayName("내가 좋아요한 게시글을 조회한다.")
    void getMyRecommendPostTest() {
        // given
        Member member = Member.builder().nickname("nickname").build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder().title("title").member(savedMember).build();
        Post savedPost = postRepository.save(post);

        Post post2 = Post.builder().title("title").member(savedMember).build();
        Post savedPost2 = postRepository.save(post2);

        RecommendPost recommendPost = RecommendPost.builder()
                .isRecommend(true)
                .member(savedMember)
                .post(savedPost)
                .build();
        recommendPostRepository.save(recommendPost);

        RecommendPost recommendPost2 = RecommendPost.builder()
                .isRecommend(true)
                .member(savedMember)
                .post(savedPost2)
                .build();
        recommendPostRepository.save(recommendPost2);

        // when
        List<MyRecommendPostServiceDto> myRecommendPost = postService.getMyRecommendPost(savedMember.getId(), 0, 10);

        // then
        assertThat(myRecommendPost).hasSize(2);
        assertThat(myRecommendPost.get(0).getTotalCount()).isEqualTo(2);
    }
}
