package community.independe.service;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    public void findByIdTest() {
        // given
        Long id = 1L;
        Comment mockComment = Comment.builder().build();

        // stub
        when(commentRepository.findById(id)).thenReturn(Optional.of(mockComment));

        // when
        Comment findComment = commentService.findById(id);

        // then
        verify(commentRepository, times(1)).findById(id);
        assertThat(findComment).isEqualTo(mockComment);
    }

    @Test
    public void findByIdFailTest() {
        // given
        Long id = 1L;

        // stub
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> commentService.findById(id));

        // then
        verify(commentRepository, times(1)).findById(id);
        assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createParentCommentTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        String content = "parentContent";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().build());

        // when
        commentService.createParentComment(memberId, postId, content);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void createParentCommentMemberFailTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        String content = "parentContent";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> commentService.createParentComment(memberId, postId, content));

        //
        verify(memberRepository).findById(memberId);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(commentRepository);
        assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createParentCommentPostFailTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        String content = "parentContent";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> commentService.createParentComment(memberId, postId, content));

        // then
        verify(memberRepository).findById(memberId);
        verify(postRepository).findById(postId);
        verifyNoInteractions(commentRepository);
        assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createChildCommentTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        String content = "childComment";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(Comment.builder().build()));
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().build());

        // when
        commentService.createChildComment(memberId, postId, commentId, content);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void createParentCommentFailTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        String content = "childContent";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> commentService.createChildComment(memberId, postId, commentId, content));

        // then
        verify(memberRepository).findById(memberId);
        verify(postRepository).findById(postId);
        verify(commentRepository).findById(postId);
        verifyNoMoreInteractions(commentRepository);
        assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
