package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.post.PostCommentResponse;
import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.CommentService;
import community.independe.service.dtos.FindCommentDto;
import community.independe.service.dtos.MyCommentServiceDto;
import community.independe.service.dtos.MyRecommendCommentServiceDto;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class CommentServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RecommendCommentRepository recommendCommentRepository;

    @Test
    @DisplayName("PK 를 이용해 댓글 정보를 조회한다.")
    void findByIdTest() {
        // given
        String content = "content";
        Member savedMember = createMember(RegionType.ULSAN);
        Post savedPost = createPost(savedMember);
        Long savedCommentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);

        // when
        FindCommentDto findCommentDto = commentService.findById(savedCommentId);

        // then
        assertThat(findCommentDto.getId()).isEqualTo(savedCommentId);
        assertThat(findCommentDto.getContent()).isEqualTo(content);
        assertThat(findCommentDto.getParentId()).isNull();
        assertThat(findCommentDto.getMemberId()).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("PK 를 잘못입력하면 댓글 조회 시 예외가 발생한다.")
    void findByIdFailTest() {
        // given
        String content = "content";
        Member savedMember = createMember(RegionType.ULSAN);
        Post savedPost = createPost(savedMember);
        Long savedCommentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.findById(savedCommentId + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("부모 댓글을 저장한다.")
    void createParentCommentTest() {
        // given
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        Member savedMember = createMember(seoul);
        Post savedPost = createPost(savedMember);

        // when
        Long savedCommentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);

        // then
        Comment findComment = commentRepository.findById(savedCommentId).get();
        assertThat(findComment.getId()).isEqualTo(savedCommentId);
        assertThat(findComment.getContent()).isEqualTo(content);
        assertThat(findComment.getParent()).isNull();
        assertThat(findComment.getMember()).isEqualTo(savedMember);
        assertThat(findComment.getPost()).isEqualTo(savedPost);
    }

    @Test
    @DisplayName("부모 댓글 저장할 때 회원 조회 실패 시 예외가 발생한다.")
    void createParentMemberFailTest() {
        // given
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        Member savedMember = createMember(seoul);
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.createParentComment(savedMember.getId() + 1L, savedPost.getId(), content))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }
    
    @Test
    @DisplayName("부모 댓글 저장할 때 게시글 조회 실패 시 예외가 발생한다.")
    void createParentCommentPostFailTest() {
        // given
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        Member savedMember = createMember(seoul);
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.createParentComment(savedMember.getId(), savedPost.getId() + 1L, content))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 작성자와 댓글 작성자가 다를 때 지역 정보가 같아야 한다.")
    void createParentCommentRegionTest() {
        // given
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        RegionType ulsan = RegionType.ULSAN;
        Member seoulMember = createMember(seoul);
        Post savedPost = createRegionPost(seoulMember, ulsan);
        Member ulsanMember = createMember(ulsan);

        // when
        Long savedCommentId = commentService.createParentComment(ulsanMember.getId(), savedPost.getId(), content);

        // then
        Comment findComment = commentRepository.findById(savedCommentId).get();
        assertThat(findComment.getId()).isEqualTo(savedCommentId);
        assertThat(findComment.getPost().getRegionType()).isEqualTo(ulsan);
    }

    @Test
    @DisplayName("게시글 작성자와 댓글 작성자가 다를 때 지역 정보가 다르면 예외가 발생한다.")
    void createParentCommentRegionFailTest() {
        // given
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        RegionType ulsan = RegionType.ULSAN;
        Member seoulMember = createMember(seoul);
        Post savedPost = createRegionPost(seoulMember, ulsan);
        Member ulsanMember = createMember(seoul);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.createParentComment(ulsanMember.getId(), savedPost.getId(), content))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REGION_NOT_AUTHENTICATE);
        });
    }

    @Test
    @DisplayName("자식 댓글을 저장한다.")
    void createChildCommentTest() {
        // given
        String content = "content";
        RegionType all = RegionType.ALL;
        Member savedMember = createMember(all);
        Post savedPost = createPost(savedMember);
        Long savedParentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);
        String childContent = "childContent";

        // when
        Long savedChildId = commentService.createChildComment(savedMember.getId(), savedPost.getId(), savedParentId, childContent);

        // then
        Comment findComment = commentRepository.findById(savedChildId).get();
        assertThat(findComment.getId()).isEqualTo(savedChildId);
        assertThat(findComment.getContent()).isEqualTo(childContent);
        assertThat(findComment.getParent().getId()).isEqualTo(savedParentId);
    }

    @Test
    @DisplayName("자식 댓글 저장할 때 회원 조회 실패 시 예외가 발생한다.")
    void createChildMemberFailTest() {
        // given
        String content = "content";
        RegionType all = RegionType.ALL;
        Member savedMember = createMember(all);
        Post savedPost = createPost(savedMember);
        Long savedParentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);
        String childContent = "childContent";

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.createChildComment(savedMember.getId() + 1L, savedPost.getId(), savedParentId, childContent))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("자식 댓글 저장할 때 게시글 조회 실패 시 예외가 발생한다.")
    void createChildCommentPostFailTest() {
        // given
        String content = "content";
        RegionType all = RegionType.ALL;
        Member savedMember = createMember(all);
        Post savedPost = createPost(savedMember);
        Long savedParentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);
        String childContent = "childContent";

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.createChildComment(savedMember.getId(), savedPost.getId() + 1L, savedParentId, childContent))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("자식 댓글 저장할 때 부모 댓글 조회 실패 시 예외가 발생한다.")
    void createChildCommentParentFailTest() {
        // given
        String content = "content";
        RegionType all = RegionType.ALL;
        Member savedMember = createMember(all);
        Post savedPost = createPost(savedMember);
        Long savedParentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);
        String childContent = "childContent";

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> commentService.createChildComment(savedMember.getId(), savedPost.getId(), savedParentId + 1L, childContent))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 작성자와 자식 댓글 작성자가 다를 때 지역 정보가 같아야 한다.")
    void createChildCommentRegionTest() {
        // given
        String content = "content";
        RegionType seoul = RegionType.SEOUL;
        RegionType ulsan = RegionType.ULSAN;
        Member seoulMember = createMember(seoul);
        Post savedPost = createRegionPost(seoulMember, ulsan);
        Long savedParentId = commentService.createParentComment(seoulMember.getId(), savedPost.getId(), content);
        Member ulsanMember = createMember(ulsan);

        // when
        Long savedCommentId = commentService.createChildComment(ulsanMember.getId(), savedPost.getId(), savedParentId, content);

        // then
        Comment findComment = commentRepository.findById(savedCommentId).get();
        assertThat(findComment.getId()).isEqualTo(savedCommentId);
        assertThat(findComment.getParent().getId()).isEqualTo(savedParentId);
        assertThat(findComment.getPost().getRegionType()).isEqualTo(ulsan);
    }

    @Test
    @DisplayName("게시글의 댓글 수를 조회한다.")
    void countAllByPostIdTest() {
        // given
        String content = "content";
        RegionType all = RegionType.ALL;
        Member savedMember = createMember(all);
        Post savedPost = createPost(savedMember);
        Long savedParentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);
        String childContent = "childContent";
        commentService.createChildComment(savedMember.getId(), savedPost.getId(), savedParentId, childContent);

        // when
        Long count = commentService.countAllByPostId(savedPost.getId());

        // then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("게시글의 댓글 정보를 조회한다.")
    void findCommentsByPostIdTest() {
        // given
        String content = "content";
        RegionType all = RegionType.ALL;
        Member savedMember = createMember(all);
        Post savedPost = createRegionPost(savedMember, all);
        Long savedParentId = commentService.createParentComment(savedMember.getId(), savedPost.getId(), content);
        String childContent = "childContent";
        commentService.createChildComment(savedMember.getId(), savedPost.getId(), savedParentId, childContent);

        // when
        List<PostCommentResponse> findComments = commentService.findCommentsByPostId(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findComments).hasSize(2);
    }

    private Member createMember(RegionType regionType) {
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .region(regionType)
                .build();
        return memberRepository.save(member);
    }

    private Post createPost(Member member) {
        Post post = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        return postRepository.save(post);
    }

    private Post createRegionPost(Member member, RegionType regionType) {
        Post post = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .regionType(regionType)
                .regionPostType(RegionPostType.TALK)
                .build();
        return postRepository.save(post);
    }

    @Test
    @DisplayName("내가 작성한 댓글을 조회한다.")
    void getMyCommentTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        postRepository.save(post);

        Comment parentComment = Comment.builder()
                .content("parent")
                .member(member)
                .post(post)
                .build();
        Comment savedComment = commentRepository.save(parentComment);

        Comment childComment = Comment.builder()
                .content("child")
                .member(member)
                .post(post)
                .parent(savedComment)
                .build();
        Comment savedChildComment = commentRepository.save(childComment);

        Post post2 = Post.builder()
                .title("title")
                .content("content")
                .member(member)
                .independentPostType(IndependentPostType.COOK)
                .build();
        postRepository.save(post2);

        Comment comment2 = Comment.builder()
                .content("comment")
                .member(member)
                .post(post2)
                .build();
        Comment savedComment2 = commentRepository.save(comment2);

        // when
        List<MyCommentServiceDto> findCommentDto = commentService.getMyComment(savedMember.getId(), 0, 10);

        // then
        assertThat(findCommentDto).hasSize(3);
        assertThat(findCommentDto.get(0).getPostId()).isNotNull();
        assertThat(findCommentDto.get(0).getTotalCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("내가 좋아요한 댓글을 조회한다.")
    void getMyRecommendCommentTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedComment = commentRepository.save(comment);

        Comment nextComment = Comment.builder()
                .content("content")
                .member(savedMember)
                .post(savedPost)
                .build();
        Comment savedNextComment = commentRepository.save(nextComment);

        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(savedMember)
                .comment(savedComment)
                .build();
        RecommendComment savedRecommendComment = recommendCommentRepository.save(recommendComment);

        RecommendComment recommendComment2 = RecommendComment.builder()
                .isRecommend(true)
                .member(savedMember)
                .comment(savedNextComment)
                .build();
        RecommendComment savedRecommendComment2 = recommendCommentRepository.save(recommendComment2);

        // when
        List<MyRecommendCommentServiceDto> myRecommendComment = commentService.getMyRecommendComment(savedMember.getId(), 0, 10);

        // then
        assertThat(myRecommendComment).hasSize(2);
        assertThat(myRecommendComment.get(0).getTotalCount()).isEqualTo(2);
    }
}
