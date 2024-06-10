package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.post.main.*;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.video.Video;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.video.VideoRepository;
import community.independe.service.MainPostService;
import community.independe.service.dtos.main.MainPostPageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class MainPostServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private MainPostService mainPostService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private VideoRepository videoRepository;

    @Test
    @DisplayName("인기 게시글을 조회한다.")
    void findPopularPostsTest() {
        // given
        Member savedMember = createMember();
        LocalDateTime dateOffset = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                createIndependentPost(savedMember, i);
            } else {
                createRegionPost(savedMember, i);
            }
        }
        MainPostPageRequest request = createMainPostPageRequest(dateOffset);

        // when
        List<PopularPostDto> findPosts = mainPostService.findPopularPosts(request);

        // then
        assertThat(findPosts).hasSize(5);
        assertThat(findPosts.get(0).getViews()).isEqualTo(9);
        assertThat(findPosts.get(0).getIndependentPostType()).isNull();
        assertThat(findPosts.get(1).getViews()).isEqualTo(8);
        assertThat(findPosts.get(1).getIndependentPostType()).isNotNull();
    }

    @Test
    @DisplayName("메인화면의 자취 게시글을 조회한다.")
    void findIndependentPostsTest() {
        // given
        Member savedMember = createMember();
        LocalDateTime dateOffset = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                createIndependentPost(savedMember, i);
            } else {
                createRegionPost(savedMember, i);
            }
        }
        MainPostPageRequest request = createMainPostPageRequest(dateOffset);

        // when
        List<PopularIndependentPostsDto> findPosts = mainPostService.findIndependentPosts(request);

        // then
        assertThat(findPosts).hasSize(5);
        assertThat(findPosts.get(0).getIndependentPostType()).isNotNull();
        assertThat(findPosts.get(1).getIndependentPostType()).isNotNull();
    }


    @Test
    @DisplayName("메인 화면의 전체 지역 게시글을 조회한다.")
    void findRegionAllPosts() {
        // given
        Member savedMember = createMember();
        LocalDateTime dateOffset = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                createRegionPost(savedMember, i);
            } else {
                createRegionAllPost(savedMember, i);
            }
        }
        MainPostPageRequest request = createMainPostPageRequest(dateOffset);

        // when
        List<RegionAllPostDto> findPosts = mainPostService.findRegionAllPosts(request);

        // then
        assertThat(findPosts).hasSize(5);
    }

    @Test
    @DisplayName("메인 화면의 전체 지역이 아닌 게시글을 조회한다.")
    void findRegionNotAllPostsTest() {
        // given
        Member savedMember = createMember();
        LocalDateTime dateOffset = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                createRegionPost(savedMember, i);
            } else {
                createRegionAllPost(savedMember, i);
            }
        }
        MainPostPageRequest request = createMainPostPageRequest(dateOffset);

        // when
        List<RegionNotAllPostDto> findPosts = mainPostService.findRegionNotAllPosts(request);

        // then
        assertThat(findPosts).hasSize(5);
        assertThat(findPosts.get(0).getRegionType()).isNotEqualTo(RegionType.ALL);
        assertThat(findPosts.get(1).getRegionType()).isNotEqualTo(RegionType.ALL);
    }

    private MainPostPageRequest createMainPostPageRequest(LocalDateTime dateOffset) {
        MainPostPageRequest request = MainPostPageRequest
                .builder()
                .dateOffset(dateOffset)
                .dateLimit(LocalDateTime.now())
                .offset(0)
                .limit(5)
                .build();
        return request;
    }

    private Member createMember() {
        Member member = Member
                .builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .region(RegionType.SEOUL)
                .role("role")
                .email("email")
                .build();
        return memberRepository.save(member);
    }

    private Post createIndependentPost(Member member, int views) {
        Post post = Post
                .builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.COOK)
                .member(member)
                .build();
        post.increaseViews(views);
        return postRepository.save(post);
    }

    private Post createRegionPost(Member member, int views) {
        Post post = Post
                .builder()
                .title("title")
                .content("content")
                .regionType(RegionType.PUSAN)
                .regionPostType(RegionPostType.RESTAURANT)
                .member(member)
                .build();
        post.increaseViews(views);
        return postRepository.save(post);
    }

    private Post createRegionAllPost(Member member, int views) {
        Post post = Post
                .builder()
                .title("title")
                .content("content")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(member)
                .build();
        post.increaseViews(views);
        return postRepository.save(post);
    }

    @Test
    @DisplayName("메인 화면의 영상을 조회한다.")
    void findAllForMainTest() {
        // given
        for (int i = 0; i < 10; i++) {
            if (i%2 == 0) {
                Video video = Video.builder()
                        .videoTitle("title")
                        .videoUrl("url")
                        .materName("master")
                        .independentPostType(IndependentPostType.COOK)
                        .views(10)
                        .build();
                videoRepository.save(video);
            } else {
                Video video = Video.builder()
                        .videoTitle("title")
                        .videoUrl("url")
                        .materName("master")
                        .independentPostType(IndependentPostType.WASH)
                        .views(10)
                        .build();
                videoRepository.save(video);
            }
        }

        // when
        List<VideoMainDto> findVideos = mainPostService.findAllForMain();

        // then
        assertThat(findVideos).hasSize(3);
    }
}
