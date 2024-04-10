package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.CommentService;
import community.independe.service.dtos.MyCommentServiceDto;
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
        List<MyCommentServiceDto> findCommentDto = commentService.getMyComment(savedMember.getId());

        // then
        assertThat(findCommentDto).hasSize(3);
        assertThat(findCommentDto.get(0).getPostId()).isNotNull();
    }
}
