package community.independe.service.manytomany;

import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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
                .isInstanceOf(CustomException.class);

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
                .isInstanceOf(CustomException.class);

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

    @Test
    void findByCommentIdAndMemberIdTest() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;
        RecommendComment mockRecommendComment = RecommendComment.builder().build();

        // stub
        when(recommendCommentRepository.findByCommentIdAndMemberId(commentId, memberId)).thenReturn(mockRecommendComment);

        // when
        RecommendComment findRecommendComment = recommendCommentService.findByCommentIdAndMemberId(commentId, memberId);

        // then
        assertThat(findRecommendComment).isEqualTo(mockRecommendComment);
        verify(recommendCommentRepository, times(1)).findByCommentIdAndMemberId(commentId, memberId);
    }

    @Test
    void countAllByCommentIdAndIsRecommendTest() {
        // given
        Long commentId = 1L;

        // stub
        when(recommendCommentRepository.countAllByCommentIdAndIsRecommend(commentId)).thenReturn(1L);

        // when
        Long count = recommendCommentService.countAllByCommentIdAndIsRecommend(commentId);

        // then
        assertThat(count).isEqualTo(1L);
        verify(recommendCommentRepository, times(1)).countAllByCommentIdAndIsRecommend(commentId);
    }

    @Test
    void findByCommentIdAndPostIdAndMemberIdAndIsRecommendTest() {
        // given
        Long commentId = 1L;
        Long postId = 1L;
        Long memberId = 1L;
        RecommendComment mockRecommendComment = RecommendComment.builder().build();

        // stub
        when(recommendCommentRepository.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId))
                .thenReturn(mockRecommendComment);

        // when
        RecommendComment findRecommendComment =
                recommendCommentService.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId);

        // then
        assertThat(findRecommendComment).isEqualTo(mockRecommendComment);
        verify(recommendCommentRepository, times(1))
                .findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId);
    }

    @Test
    void findBestCommentTest() {
        // given
        List<Object[]> commentObjects = new ArrayList<>();

        // stub
        when(recommendCommentRepository.findBestComment()).thenReturn(commentObjects);

        // when
        List<Object[]> bestComment = recommendCommentService.findBestComment();

        // then
        assertThat(bestComment).isEqualTo(commentObjects);
        verify(recommendCommentRepository, times(1)).findBestComment();
    }
}
