package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.util.PageRequestCreator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PostRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RecommendPostRepository recommendPostRepository;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void initData() {
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        Post independentPost = Post.builder()
                .title("independentTitle")
                .content("independentContent")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post independentPost2 = Post.builder()
                .title("independentTitle2")
                .content("independentContent2")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post independentPost3 = Post.builder()
                .title("independentTitle3")
                .content("independentContent3")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();

        Post regionPost = Post.builder()
                .title("regionTitle")
                .content("regionContent")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(savedMember)
                .build();
        Post regionPost2 = Post.builder()
                .title("regionTitle2")
                .content("regionContent2")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(savedMember)
                .build();

        Post savedIndependentPost = postRepository.save(independentPost);
        Post savedIndependentPost2 = postRepository.save(independentPost2);
        Post savedIndependentPost3 = postRepository.save(independentPost3);
        Post savedRegionPost = postRepository.save(regionPost);
        Post savedRegionPost2 = postRepository.save(regionPost2);
    }

    @Test
    public void saveTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        Post independentPost = Post.builder()
                .title("saveIndependentTitle")
                .content("saveIndependentContent")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();

        Post regionPost = Post.builder()
                .title("saveRegionTitle")
                .content("saveRegionContent")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(savedMember)
                .build();

        // when
        Post savedIndependentPost = postRepository.save(independentPost);
        Post savedRegionPost = postRepository.save(regionPost);

        //then
        assertThat(savedIndependentPost.getTitle()).isEqualTo("saveIndependentTitle");
        assertThat(savedRegionPost.getTitle()).isEqualTo("saveRegionTitle");
        assertThat(savedRegionPost.getRegionType().getDescription()).isEqualTo(regionPost.getRegionType().getDescription());
        assertThat(savedRegionPost.getRegionPostType().getDescription()).isEqualTo(regionPost.getRegionPostType().getDescription());
        assertThat(savedIndependentPost.getIndependentPostType().getDescription()).isEqualTo(independentPost.getIndependentPostType().getDescription());
    }

    @Test
    void findAllRegionPostsByTypesWithMemberDynamicTest() {
        // given
        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;
        String condition = "";
        String keyword = "";
        PageRequest page = PageRequest.of(0, 5);

        // when
        Page<Post> allRegionPostsByTypesWithMemberDynamic = postRepository.findAllRegionPostsByTypesWithMemberDynamic(regionType, regionPostType, condition, keyword, page);
        List<Post> content = allRegionPostsByTypesWithMemberDynamic.getContent();

        // then
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(1).getTitle()).isEqualTo("regionTitle");
        assertThat(content.get(1).getContent()).isEqualTo("regionContent");
        assertThat(content.get(0).getTitle()).isEqualTo("regionTitle2");
        assertThat(content.get(0).getContent()).isEqualTo("regionContent2");
    }

    @Test
    void findAllRegionPostsByTypesWithMemberDynamicSearchTest() {
        // given
        Member findMember = memberRepository.findByUsername("id");
        Post regionPost = Post.builder()
                .title("SearchTitle")
                .content("SearchContent")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(findMember)
                .build();

        postRepository.save(regionPost);

        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;
        String condition = "title";
        String keyword = "Search";
        PageRequest page = PageRequest.of(0, 5);

        // when
        Page<Post> allRegionPostsByTypesWithMemberDynamic = postRepository.findAllRegionPostsByTypesWithMemberDynamic(regionType, regionPostType, condition, keyword, page);
        List<Post> content = allRegionPostsByTypesWithMemberDynamic.getContent();

        // then
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getTitle()).isEqualTo("SearchTitle");
        assertThat(content.get(0).getContent()).isEqualTo("SearchContent");
    }

    @Test
    void findAllIndependentPostsByTypeWithMemberDynamicTest() {
        // given
        IndependentPostType independentPostType = IndependentPostType.COOK;
        String condition = "";
        String keyword = "";
        PageRequest page = PageRequest.of(0, 5);

        // when
        Page<Post> allIndependentPostsByTypeWithMemberDynamic =
                postRepository.findAllIndependentPostsByTypeWithMemberDynamic(independentPostType, condition, keyword, page);
        List<Post> content = allIndependentPostsByTypeWithMemberDynamic.getContent();

        // then
        assertThat(content.size()).isEqualTo(3);
        assertThat(content.get(0).getTitle()).isEqualTo("independentTitle3");
        assertThat(content.get(0).getContent()).isEqualTo("independentContent3");
        assertThat(content.get(1).getTitle()).isEqualTo("independentTitle2");
        assertThat(content.get(1).getContent()).isEqualTo("independentContent2");
        assertThat(content.get(2).getTitle()).isEqualTo("independentTitle");
        assertThat(content.get(2).getContent()).isEqualTo("independentContent");
    }

    @Test
    void findAllPostsBySearchWithMemberDynamic() {
        // given
        String condition = "title";
        String keyword = "Title";
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<Post> allPostsBySearchWithMemberDynamic =
                postRepository.findAllPostsBySearchWithMemberDynamic(condition, keyword, page);

        List<Post> content = allPostsBySearchWithMemberDynamic.getContent();

        // then
        assertThat(content.size()).isEqualTo(5);
        assertThat(content.get(0).getTitle()).isEqualTo("regionTitle2");
        assertThat(content.get(0).getContent()).isEqualTo("regionContent2");
        assertThat(content.get(1).getTitle()).isEqualTo("regionTitle");
        assertThat(content.get(1).getContent()).isEqualTo("regionContent");
        assertThat(content.get(2).getTitle()).isEqualTo("independentTitle3");
        assertThat(content.get(2).getContent()).isEqualTo("independentContent3");
        assertThat(content.get(3).getTitle()).isEqualTo("independentTitle2");
        assertThat(content.get(3).getContent()).isEqualTo("independentContent2");
        assertThat(content.get(4).getTitle()).isEqualTo("independentTitle");
        assertThat(content.get(4).getContent()).isEqualTo("independentContent");
    }

    @Test
    void deletePostByPostIdTest() {
        // given
        Post findPost = postRepository.findAll().get(0);
        Long findPostId = findPost.getId();

        // when
        postRepository.deletePostByPostId(findPostId);

        // then
        assertThat(postRepository.findAll().size()).isEqualTo(4);
    }

    @Test
    void judgeConditionTest() {
        // given
        String[] conditions = {"title", "nickname", "all", "content", "total", "other"};
        String keyword = "mock";
        PageRequest page = PageRequest.of(0, 10);
        Member mockUser = Member.builder()
                .username("mockId")
                .password("mockPassword")
                .nickname("mockNickname")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(mockUser);

        Post mockPost = Post.builder()
                .title("mockTitle")
                .content("mockContent")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        postRepository.save(mockPost);

        for (String condition : conditions) {
            // when
            Page<Post> findPostsPage =
                    postRepository.findAllPostsBySearchWithMemberDynamic(condition, keyword, page);
            List<Post> findPosts = findPostsPage.getContent();

            // then
            if(condition.equals("other")) {
                assertThat(findPosts.size()).isEqualTo(6);
            } else {
                assertThat(findPosts.size()).isEqualTo(1);
            }
        }
    }

    @Test
    void judgeConditionNoKeywordTest() {
        // given
        String[] conditions = {"title", "nickname", "all", "content", "total", "other"};
        String keyword = "";
        PageRequest page = PageRequest.of(0, 10);

        // when
        for (String condition : conditions) {
            // when
            Page<Post> findPostsPage =
                    postRepository.findAllPostsBySearchWithMemberDynamic(condition, keyword, page);
            List<Post> findPosts = findPostsPage.getContent();

            // then
            assertThat(findPosts.size()).isEqualTo(5);
        }
    }

    @Test
    @DisplayName("회원 PK를 통해 작성한 게시글을 조회한다.")
    void findAllByMemberIdTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder().member(savedMember).build();
        Post savedPost = postRepository.save(post);
        Post post2 = Post.builder().member(savedMember).build();
        Post savedPost2 = postRepository.save(post2);

        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(0, 10);

        // when
        Page<Post> findPostPage = postRepository.findAllByMemberId(savedMember.getId(), request);
        List<Post> findPosts = findPostPage.getContent();

        // then
        assertThat(findPosts).hasSize(2);
    }

    @Test
    @DisplayName("회원 PK를 통해 추천한 게시글을 조회한다.")
    void findRecommendPostByMemberIdTest() {
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
        PageRequest request = PageRequestCreator.createPageRequestSortCreatedDateDesc(0, 10);

        // when
        Page<Post> recommendPostByMemberId = postRepository.findRecommendPostByMemberId(savedMember.getId(), request);
        List<Post> findPosts = recommendPostByMemberId.getContent();

        // then
        assertThat(findPosts).hasSize(2);
    }
}
