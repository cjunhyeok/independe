package community.independe.service.integration.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.manytomany.RecommendPostService;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class RecommendPostServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private RecommendPostService recommendPostService;
    @Autowired
    private RecommendPostRepository recommendPostRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 추천을 저장한다.")
    void saveTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);

        // when
        Long savedRecommendPostId = recommendPostService.save(savedPost.getId(), savedMember.getId());

        // then
        RecommendPost findRecommendPost = recommendPostRepository.findById(savedRecommendPostId).get();
        assertThat(findRecommendPost.getId()).isEqualTo(savedRecommendPostId);
        assertThat(findRecommendPost.getPost()).isEqualTo(savedPost);
        assertThat(findRecommendPost.getMember()).isEqualTo(savedMember);
        assertThat(findRecommendPost.getIsRecommend()).isTrue();
    }

    @Test
    @DisplayName("게시글 추천 저장 시 회원 PK 를 잘못 입력하면 예외가 발생한다.")
    void saveMemberFailTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> recommendPostService.save(savedPost.getId(), savedMember.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 추천 시 게시글 PK 를 잘못 입력하면 예외가 발생한다.")
    void savePostFailTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> recommendPostService.save(savedPost.getId() + 1L, savedMember.getId()))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 추천 저장 후 게시글 추천 시 추천 여부가 false 가 된다.")
    void saveIsRecommendFalseTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);
        recommendPostService.save(savedPost.getId(), savedMember.getId());

        // when
        Long savedRecommendPostId = recommendPostService.save(savedPost.getId(), savedMember.getId());

        // then
        RecommendPost findRecommendPost = recommendPostRepository.findById(savedRecommendPostId).get();
        assertThat(findRecommendPost.getId()).isEqualTo(savedRecommendPostId);
        assertThat(findRecommendPost.getPost()).isEqualTo(savedPost);
        assertThat(findRecommendPost.getMember()).isEqualTo(savedMember);
        assertThat(findRecommendPost.getIsRecommend()).isFalse();
    }

    @Test
    @DisplayName("게시글 추천 저장 후 게시글 추천 시 추천 여부가 false 이면 true 가 된다.")
    void saveIsRecommendTrueTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);
        recommendPostService.save(savedPost.getId(), savedMember.getId());
        recommendPostService.save(savedPost.getId(), savedMember.getId());

        // when
        Long savedRecommendPostId = recommendPostService.save(savedPost.getId(), savedMember.getId());

        // then
        RecommendPost findRecommendPost = recommendPostRepository.findById(savedRecommendPostId).get();
        assertThat(findRecommendPost.getId()).isEqualTo(savedRecommendPostId);
        assertThat(findRecommendPost.getPost()).isEqualTo(savedPost);
        assertThat(findRecommendPost.getMember()).isEqualTo(savedMember);
        assertThat(findRecommendPost.getIsRecommend()).isTrue();
    }

    @Test
    @DisplayName("게시글 PK 를 통해 게시글 추천 수를 조회한다.")
    void countByPostIdTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Member savedMember2 = createMember("username2", "nickname2");
        Member savedMember3 = createMember("username3", "nickname3");
        Post savedPost = createPost(savedMember);
        recommendPostService.save(savedPost.getId(), savedMember.getId());
        recommendPostService.save(savedPost.getId(), savedMember.getId());
        recommendPostService.save(savedPost.getId(), savedMember2.getId());
        recommendPostService.save(savedPost.getId(), savedMember3.getId());

        // when
        Long recommendPostCount = recommendPostService.countByPostId(savedPost.getId());

        // then
        assertThat(recommendPostCount).isEqualTo(2);
    }

    private Member createMember(String username, String nickname) {
        Member member = Member.builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .build();
        return memberRepository.save(member);
    }

    private Post createPost(Member member) {
        Post post = Post
                .builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(member)
                .build();
        return postRepository.save(post);
    }
}
