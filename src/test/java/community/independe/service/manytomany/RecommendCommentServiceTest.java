package community.independe.service.manytomany;

import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.exception.notfound.CommentNotFountException;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendCommentServiceTest {

    @InjectMocks
    private RecommendCommentServiceImpl recommendCommentService;
    @Mock
    private RecommendCommentRepository recommendCommentRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void saveTest() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        // stub
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(Comment.builder().build()));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(recommendCommentRepository.save(any(RecommendComment.class))).thenReturn(RecommendComment.builder().build());

        // when
        recommendCommentService.save(commentId, memberId);

        // then
        verify(commentRepository, times(1)).findById(memberId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(recommendCommentRepository, times(1)).save(any(RecommendComment.class));
    }

    @Test
    void saveCommentFailTest() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        // stub
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> recommendCommentService.save(commentId, memberId))
                .isInstanceOf(CommentNotFountException.class)
                .hasMessage("Comment Not Exist");

        // then
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void saveMemberFailTest() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        // stub
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(Comment.builder().build()));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> recommendCommentService.save(commentId, memberId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(commentRepository, times(1)).findById(commentId);
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void updateIsRecommendTest() {
        // given
        RecommendComment recommendComment = RecommendComment.builder().isRecommend(false).build();
        Boolean isRecommend = true;

        // when
        recommendCommentService.updateIsRecommend(recommendComment, isRecommend);

        // then
        assertThat(recommendComment.getIsRecommend()).isEqualTo(isRecommend);
    }
}
