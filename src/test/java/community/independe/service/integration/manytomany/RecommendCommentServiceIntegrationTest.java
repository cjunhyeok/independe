package community.independe.service.integration.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.manytomany.RecommendCommentService;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class RecommendCommentServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private RecommendCommentService recommendCommentService;
    @Autowired
    private RecommendCommentRepository recommendCommentRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("댓글 추천을 저장한다.")
    void saveTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        Comment savedComment = createComment(savedMember, savedPost);

        // when
        Long savedRecommendCommentId
                = recommendCommentService.save(savedComment.getId(), savedMember.getId());

        // then
        RecommendComment findRecommendComment = recommendCommentRepository.findById(savedRecommendCommentId).get();
        assertThat(findRecommendComment.getId()).isEqualTo(savedRecommendCommentId);
        assertThat(findRecommendComment.getMember()).isEqualTo(savedMember);
        assertThat(findRecommendComment.getComment()).isEqualTo(savedComment);
        assertThat(findRecommendComment.getIsRecommend()).isTrue();
    }

    @Test
    @DisplayName("댓글 추천 저장 시 회원 PK 를 잘못 입력하면 예외가 발생한다.")
    void saveMemberFailTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        Comment savedComment = createComment(savedMember, savedPost);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> recommendCommentService.save(savedComment.getId(), savedMember.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("댓글 추천 저장 시 댓글 PK 를 잘못 입력하면 예외가 발생한다.")
    void saveCommentFailTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        Comment savedComment = createComment(savedMember, savedPost);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> recommendCommentService.save(savedComment.getId() + 1L, savedMember.getId()))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("댓글 추천 저장 후 댓글추천 시 추쳔 여부가 false 가 된다.")
    void saveIsRecommendFalseTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        Comment savedComment = createComment(savedMember, savedPost);
        recommendCommentService.save(savedComment.getId(), savedMember.getId());

        // when
        Long savedRecommendCommentId
                = recommendCommentService.save(savedComment.getId(), savedMember.getId());

        // then
        RecommendComment findRecommendComment = recommendCommentRepository.findById(savedRecommendCommentId).get();
        assertThat(findRecommendComment.getId()).isEqualTo(savedRecommendCommentId);
        assertThat(findRecommendComment.getMember()).isEqualTo(savedMember);
        assertThat(findRecommendComment.getComment()).isEqualTo(savedComment);
        assertThat(findRecommendComment.getIsRecommend()).isFalse();
    }

    @Test
    @DisplayName("댓글 추천 저장 후 댓글추천 시 추쳔 여부가 false 이면 true 가 된다.")
    void saveIsRecommendTrueTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        Comment savedComment = createComment(savedMember, savedPost);
        recommendCommentService.save(savedComment.getId(), savedMember.getId());
        recommendCommentService.save(savedComment.getId(), savedMember.getId());

        // when
        Long savedRecommendCommentId
                = recommendCommentService.save(savedComment.getId(), savedMember.getId());

        // then
        RecommendComment findRecommendComment = recommendCommentRepository.findById(savedRecommendCommentId).get();
        assertThat(findRecommendComment.getId()).isEqualTo(savedRecommendCommentId);
        assertThat(findRecommendComment.getMember()).isEqualTo(savedMember);
        assertThat(findRecommendComment.getComment()).isEqualTo(savedComment);
        assertThat(findRecommendComment.getIsRecommend()).isTrue();
    }

    // todo findBestComment Test

    private Member createMember() {
        Member member = Member.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
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

    private Comment createComment(Member member, Post post) {
        Comment comment = Comment.builder()
                .content("content")
                .member(member)
                .post(post)
                .build();
        return commentRepository.save(comment);
    }
}
