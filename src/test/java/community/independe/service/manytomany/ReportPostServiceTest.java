package community.independe.service.manytomany;

import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.CustomException;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.ReportPostRepository;
import community.independe.repository.post.PostRepository;
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
public class ReportPostServiceTest {

    @InjectMocks
    private ReportPostServiceImpl reportPostService;
    @Mock
    private ReportPostRepository reportPostRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    void saveTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member mockMember = Member.builder().build();
        Post mockPost = Post.builder().member(mockMember).build();
        ReportPost mockReportPost =
                ReportPost.builder().member(mockMember).post(mockPost).build();

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(reportPostRepository.save(any(ReportPost.class))).thenReturn(mockReportPost);

        // when
        reportPostService.save(postId, memberId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(reportPostRepository, times(1)).save(any(ReportPost.class));
    }

    @Test
    void savePostFailTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> reportPostService.save(postId, memberId))
                .isInstanceOf(CustomException.class);

        // then
        verify(postRepository, times(1)).findById(postId);
        verifyNoInteractions(memberRepository);
        verifyNoInteractions(reportPostRepository);
    }

    @Test
    void saveMemberFailTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> reportPostService.save(postId, memberId))
                .isInstanceOf(CustomException.class);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(memberRepository, times(1)).findById(memberId);
        verifyNoInteractions(reportPostRepository);
    }

    @Test
    void updateIsReportTest() {
        // given
        ReportPost mockReportPost =
                ReportPost.builder().isReport(true).build();
        Boolean isReport = false;

        // when
        reportPostService.updateIsReport(mockReportPost, isReport);

        // then
        assertThat(mockReportPost.getIsReport()).isEqualTo(isReport);
    }

    @Test
    void findByPostIdAndMemberIdTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        ReportPost mockReportPost = ReportPost
                .builder()
                .post(Post.builder().build())
                .build();

        // stub
        when(reportPostRepository.findByPostIdAndMemberId(postId, memberId)).thenReturn(mockReportPost);

        // when
        ReportPost findReportPost = reportPostService.findByPostIdAndMemberId(postId, memberId);

        // then
        assertThat(findReportPost).isEqualTo(mockReportPost);
        verify(reportPostRepository, times(1)).findByPostIdAndMemberId(postId, memberId);
    }

    @Test
    void findByPostIdAndMemberIdAndIsRecommendTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        ReportPost mockReportPost = ReportPost
                .builder()
                .post(Post.builder().build())
                .isReport(true)
                .build();

        // stub
        when(reportPostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId)).thenReturn(mockReportPost);

        // when
        ReportPost findReportPost = reportPostService.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);

        // then
        assertThat(findReportPost).isEqualTo(mockReportPost);
        verify(reportPostRepository, times(1)).findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
    }
}
