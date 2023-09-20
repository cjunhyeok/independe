package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
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
public class RecommendPostServiceTest {

    @InjectMocks
    private RecommendPostServiceImpl recommendPostService;
    @Mock
    private RecommendPostRepository recommendPostRepository;
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
        RecommendPost mockRecommendPost = RecommendPost.builder().post(mockPost).member(mockMember).build();

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(recommendPostRepository.save(any(RecommendPost.class))).thenReturn(mockRecommendPost);

        // when
        recommendPostService.save(postId, memberId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(recommendPostRepository, times(1)).save(any(RecommendPost.class));
    }

    @Test
    void savePostFailTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> recommendPostService.save(postId, memberId))
                .isInstanceOf(PostNotFountException.class)
                .hasMessage("Post Not Exist");

        // then
        verify(postRepository, times(1)).findById(postId);
        verifyNoInteractions(memberRepository);
        verifyNoInteractions(recommendPostRepository);
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
        assertThatThrownBy(() -> recommendPostService.save(postId, memberId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(memberRepository, times(1)).findById(memberId);
        verifyNoInteractions(recommendPostRepository);
    }

    @Test
    void updateIsRecommendTest() {
        // given
        Member mockMember = Member.builder().build();
        Post mockPost = Post.builder().member(mockMember).build();
        RecommendPost mockRecommendPost =
                RecommendPost.builder().post(mockPost).member(mockMember).isRecommend(false).build();
        Boolean isRecommend = true;

        // when
        recommendPostService.updateIsRecommend(mockRecommendPost, isRecommend);

        // then
        assertThat(mockRecommendPost.getIsRecommend()).isEqualTo(isRecommend);
    }
}
