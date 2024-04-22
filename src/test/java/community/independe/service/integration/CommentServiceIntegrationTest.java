package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.CommentService;
import community.independe.service.dtos.MyCommentServiceDto;
import community.independe.service.dtos.MyRecommendCommentServiceDto;
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