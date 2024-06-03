package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.post.PostsResponse;
import community.independe.api.dtos.post.SearchResponse;
import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.PostService;
import community.independe.service.dtos.MyPostServiceDto;
import community.independe.service.dtos.MyRecommendPostServiceDto;
import community.independe.service.dtos.post.FindAllPostsDto;
import community.independe.service.dtos.post.FindIndependentPostsDto;
import community.independe.service.dtos.post.FindPostDto;
import community.independe.service.dtos.post.FindRegionPostsDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class PostServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RecommendPostRepository recommendPostRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("PK 로 게시글 정보를 조회한다.")
    void findByIdTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType clean = IndependentPostType.CLEAN;
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, IndependentPostType.CLEAN);

        // when
        FindPostDto findPostDto = postService.findById(savedPostId);

        // then
        assertThat(findPostDto.getId()).isEqualTo(savedPostId);
        assertThat(findPostDto.getTitle()).isEqualTo(title);
        assertThat(findPostDto.getContent()).isEqualTo(content);
        assertThat(findPostDto.getIndependentPostType()).isEqualTo(clean);
        assertThat(findPostDto.getRegionType()).isNull();
        assertThat(findPostDto.getRegionPostType()).isNull();
        assertThat(findPostDto.getMemberId()).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("PK 를 잘못입력 하면 게시글 조회 시 예외가 발생한다.")
    void findByIdFailTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType clean = IndependentPostType.CLEAN;
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, IndependentPostType.CLEAN);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> postService.findById(savedPostId + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("자취 게시글을 저장한다.")
    void createIndependentPostTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType etc = IndependentPostType.ETC;

        // when
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, etc);

        // then
        Post findPost = postRepository.findById(savedPostId).get();
        assertThat(findPost.getId()).isEqualTo(savedPostId);
        assertThat(findPost.getTitle()).isEqualTo(title);
        assertThat(findPost.getContent()).isEqualTo(content);
        assertThat(findPost.getIndependentPostType()).isEqualTo(etc);
        assertThat(findPost.getRegionType()).isNull();
        assertThat(findPost.getRegionPostType()).isNull();
        assertThat(findPost.getMember()).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("회원 정보가 없으면 자취 게시글 저장 시 예외가 발생한다.")
    void createIndependentPostFailTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType cook = IndependentPostType.COOK;

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> postService.createIndependentPost(savedMember.getId() + 1L, title, content, cook))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("지역 게시글을 저장한다.")
    void createRegionPostTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        RegionPostType free = RegionPostType.FREE;

        // when
        Long savedPostId
                = postService.createRegionPost(savedMember.getId(), title, content, seoul, free);

        // then
        Post findPost = postRepository.findById(savedPostId).get();
        assertThat(findPost.getId()).isEqualTo(savedPostId);
        assertThat(findPost.getTitle()).isEqualTo(title);
        assertThat(findPost.getContent()).isEqualTo(content);
        assertThat(findPost.getIndependentPostType()).isNull();
        assertThat(findPost.getRegionType()).isEqualTo(seoul);
        assertThat(findPost.getRegionPostType()).isEqualTo(free);
        assertThat(findPost.getMember()).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("회원 정보가 없으면 지역 게시글 저장 시 예외가 발생한다.")
    void createRegionPostFailTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        RegionType ulsan = RegionType.ULSAN;
        RegionPostType restaurant = RegionPostType.RESTAURANT;

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> postService.createRegionPost(savedMember.getId() + 1L, title, content, ulsan, restaurant))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 정보를 수정한다.")
    void updatePostTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType wash = IndependentPostType.WASH;
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, wash);
        String updateTitle = "updateTitle";
        String updateContent = "updateContent";

        // when
        postService.updatePost(savedPostId, updateTitle, updateContent);

        // then
        Post findPost = postRepository.findById(savedPostId).get();
        assertThat(findPost.getId()).isEqualTo(savedPostId);
        assertThat(findPost.getTitle()).isEqualTo(updateTitle);
        assertThat(findPost.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("PK 를 잘못입력 하면 게시글 수정 시 예외가 발생한다.")
    void updatePostFailTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType wash = IndependentPostType.WASH;
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, wash);
        String updateTitle = "updateTitle";
        String updateContent = "updateContent";

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> postService.updatePost(savedPostId + 1L, updateTitle, updateContent))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("PK 로 게시글을 삭제한다.")
    void deletePostTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType etc = IndependentPostType.ETC;
        Post post = Post.builder()
                .title(title)
                .content(content)
                .independentPostType(etc)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);
        Comment comment = Comment
                .builder()
                .content(content)
                .post(savedPost)
                .member(savedMember)
                .build();
        Comment savedComment = commentRepository.save(comment);


        // when
        postService.deletePost(savedPost.getId());

        // 영속성 context 삭제
        em.clear();
        em.flush();

        // then
        Optional<Post> findPostOptional = postRepository.findById(savedPost.getId());
        assertThat(findPostOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("PK 를 잘못 입력하면 게시글 삭제 시 예외가 발생한다.")
    void deletePostFailTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType etc = IndependentPostType.ETC;
        Post post = Post.builder()
                .title(title)
                .content(content)
                .independentPostType(etc)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> postService.deletePost(savedPost.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("자취 게시글들을 조회한다.")
    void findIndependentPostsTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";

        IndependentPostType etc = IndependentPostType.ETC;
        postService.createIndependentPost(savedMember.getId(), title, content, etc);
        postService.createIndependentPost(savedMember.getId(), title, content, etc);

        RegionType all = RegionType.ALL;
        RegionPostType free = RegionPostType.FREE;
        postService.createRegionPost(savedMember.getId(), title, content, all, free);
        postService.createRegionPost(savedMember.getId(), title, content, all, free);

        FindIndependentPostsDto findIndependentPostsDto = FindIndependentPostsDto
                .builder()
                .independentPostType(etc)
                .condition(title)
                .keyword(title)
                .page(0)
                .size(10)
                .build();

        // when
        List<PostsResponse> independentPosts = postService.findIndependentPosts(findIndependentPostsDto);

        // then
        assertThat(independentPosts).hasSize(2);
    }

    @Test
    @DisplayName("지역 게시글들을 조회한다.")
    void findRegionPostsTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";

        IndependentPostType etc = IndependentPostType.ETC;
        postService.createIndependentPost(savedMember.getId(), title, content, etc);
        postService.createIndependentPost(savedMember.getId(), title, content, etc);

        RegionType all = RegionType.ALL;
        RegionPostType free = RegionPostType.FREE;
        postService.createRegionPost(savedMember.getId(), title, content, all, free);
        postService.createRegionPost(savedMember.getId(), title, content, all, free);

        FindRegionPostsDto findRegionPostsDto = FindRegionPostsDto
                .builder()
                .regionType(all)
                .regionPostType(free)
                .condition(title)
                .keyword(title)
                .page(0)
                .size(10)
                .build();

        // when
        List<PostsResponse> independentPosts = postService.findRegionPosts(findRegionPostsDto);

        // then
        assertThat(independentPosts).hasSize(2);
    }

    @Test
    @DisplayName("전체 게시글을 조회한다.")
    void findAllPostsTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";

        IndependentPostType etc = IndependentPostType.ETC;
        postService.createIndependentPost(savedMember.getId(), title, content, etc);
        postService.createIndependentPost(savedMember.getId(), title, content, etc);

        RegionType all = RegionType.ALL;
        RegionPostType free = RegionPostType.FREE;
        postService.createRegionPost(savedMember.getId(), title, content, all, free);
        postService.createRegionPost(savedMember.getId(), title, content, all, free);

        FindAllPostsDto findAllPostsDto = FindAllPostsDto
                .builder()
                .condition(title)
                .keyword(title)
                .page(0)
                .size(10)
                .build();

        // when
        List<SearchResponse> allPosts = postService.findAllPosts(findAllPostsDto);

        // then
        assertThat(allPosts).hasSize(4);
    }

    @Test
    @DisplayName("게시글 조회 수를 증가시킨다.")
    void increaseViewsTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType health = IndependentPostType.HEALTH;
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, health);

        // when
        postService.increaseViews(savedPostId);

        // then
        Post findPost = postRepository.findById(savedPostId).get();
        assertThat(findPost.getViews()).isEqualTo(1);
    }

    @Test
    @DisplayName("PK 를 잘못입력 하면 게시글 조회 수 증가 시 예외가 발생한다.")
    void increaseViewsFailTest() {
        // given
        Member savedMember = createMember();
        String title = "title";
        String content = "content";
        IndependentPostType health = IndependentPostType.HEALTH;
        Long savedPostId = postService.createIndependentPost(savedMember.getId(), title, content, health);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> postService.increaseViews(savedPostId + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

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

    private Member createMember() {
        Member member = Member.builder().username("username").nickname("nickname").build();
        return memberRepository.save(member);
    }
}
