package community.independe.service;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.notfound.CommentNotFountException;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThatThrownBy(() -> commentService.findById(id))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    public void createParentCommentTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        String content = "content";
        Member mockMember = Member.builder().region(RegionType.SEOUL).build();
        Post mockPost = Post.builder().regionType(RegionType.SEOUL).member(Member.builder().build()).build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
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
        assertThatThrownBy(() -> commentService.createParentComment(memberId, postId, content))
                .isInstanceOf(MemberNotFountException.class);

        //
        verify(memberRepository).findById(memberId);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(commentRepository);
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
        assertThatThrownBy(() -> commentService.createParentComment(memberId, postId, content))
                .isInstanceOf(PostNotFountException.class);

        // then
        verify(memberRepository).findById(memberId);
        verify(postRepository).findById(postId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    public void createChildCommentTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        String content = "childComment";

        Member mockMember = Member.builder().region(RegionType.SEOUL).build();
        Post mockPost = Post.builder().regionType(RegionType.SEOUL).member(Member.builder().build()).build();

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
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
    public void createChildCommentMemberFailTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        String content = "childContent";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> commentService.createChildComment(memberId, postId, commentId, content))
                .isInstanceOf(MemberNotFountException.class);

        //
        verify(memberRepository).findById(memberId);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    public void createChildCommentPostFailTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Long commentId = 1L;
        String content = "childContent";

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> commentService.createChildComment(memberId, postId, commentId, content))
                .isInstanceOf(PostNotFountException.class);

        // then
        verify(memberRepository).findById(memberId);
        verify(postRepository).findById(postId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    public void createChildCommentCommentFailTest() {
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
        assertThatThrownBy(() -> commentService.createChildComment(memberId, postId, commentId, content))
                .isInstanceOf(CommentNotFountException.class);

        // then
        verify(memberRepository).findById(memberId);
        verify(postRepository).findById(postId);
        verify(commentRepository).findById(postId);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void checkRegionFailTest() {
        // given
        Member mockMember = Member.builder().region(RegionType.SEOUL).build();
        Post mockPost = Post.builder().regionType(RegionType.PUSAN).member(Member.builder().build()).build();

        // stub
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        // when
        AbstractThrowableAssert<?, ? extends Throwable> abstractThrowableAssert
                = assertThatThrownBy(() -> commentService.createParentComment(1L, 1L, "content"));

        // then
        abstractThrowableAssert
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region Not Authenticate");
    }

    @Test
    void checkRegionIsRegionFalseTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        String content = "content";
        Member mockMember = Member.builder().region(RegionType.SEOUL).build();
        Post mockIndependentPost = Post.builder().independentPostType(IndependentPostType.COOK).member(Member.builder().build()).build();
        Post mockRegionAllPost = Post.builder().regionType(RegionType.ALL).member(Member.builder().build()).build();
        Post posts[] = new Post[]{mockIndependentPost, mockRegionAllPost};

        for (Post post : posts) {
            // stub
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().build());

            // when
            commentService.createParentComment(memberId, postId, content);

            // then
            verify(postRepository, times(1)).findById(postId);
            postId++;
        }

        verify(memberRepository, times(2)).findById(memberId);
        verify(commentRepository, times(2)).save(any(Comment.class));
    }
}
