package community.independe.service.integration.util;

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
import community.independe.service.util.ActionStatusChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class ActionStatusCheckerTest extends IntegrationTestSupporter {

    @Autowired
    private ActionStatusChecker actionStatusChecker;
    @Autowired
    private RecommendCommentRepository recommendCommentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 추천 엔티티 조회 후 null 이 아니면 true 를 반환한다.")
    void isRecommendCommentTrueTest() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = createComment(member, post);
        RecommendComment recommendComment = createRecommendComment(member, comment);

        // when
        boolean isRecommend = actionStatusChecker.isRecommendComment(comment.getId(), post.getId(), member.getId());

        // then
        assertThat(isRecommend).isTrue();
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        return memberRepository.save(member);
    }

    private Post createPost(Member member) {
        Post post = Post.builder()
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

    private RecommendComment createRecommendComment(Member member, Comment comment) {
        RecommendComment recommendComment = RecommendComment.builder()
                .isRecommend(true)
                .member(member)
                .comment(comment)
                .build();
        return recommendCommentRepository.save(recommendComment);
    }
}
