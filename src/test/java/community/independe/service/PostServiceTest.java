package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    public void findByIdTest() {
        // given
        Long postId = 1L;
        Post mockPost = Post.builder().build();

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // when
        Post findPost = postService.findById(postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        assertThat(findPost).isEqualTo(mockPost);
    }

    @Test
    public void findByIdFailTest() {
        // given
        Long postId = 1L;

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> postService.findById(postId))
                .isInstanceOf(PostNotFountException.class);

        // then
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void createIndependentPostTest() {
        // given
        Long memberId = 1L;
        String title = "independentTitle";
        String content = "independentContent";
        IndependentPostType independentPostType = IndependentPostType.COOK;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.save(any(Post.class))).thenReturn(Post.builder().build());

        // when
        Long independentPostId = postService.createIndependentPost(memberId, title, content, independentPostType);

        verify(memberRepository).findById(memberId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    public void createRegionPostTest() {
        // given
        Long memberId = 1L;
        String title = "regionTitle";
        String content = "regionContent";
        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(postRepository.save(any(Post.class))).thenReturn(Post.builder().build());

        // when
        postService.createRegionPost(memberId, title, content, regionType, regionPostType);

        // then
        verify(memberRepository).findById(memberId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    public void createPostFailTest() {
        // given
        Long memberId = 1L;
        String title = "regionTitle";
        String content = "regionContent";
        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> postService.createRegionPost(memberId, title, content, regionType, regionPostType))
                .isInstanceOf(MemberNotFountException.class);

        // then
        verify(memberRepository).findById(memberId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void updatePostTest() {
        // given
        Long postId = 1L;
        String title = "updateTitle";
        String content = "updateContent";
        Post mockPost = Post.builder().build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // when
        Long updatedPostId = postService.updatePost(postId, title, content);

        assertThat(mockPost.getTitle()).isEqualTo(title);
        assertThat(mockPost.getContent()).isEqualTo(content);

        // then
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void increaseViewsTest() {
        // given
        Long postId = 1L;
        Post mockPost = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.COOK)
                .build();

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // when
        postService.increaseViews(postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        assertThat(1).isEqualTo(mockPost.getViews());
    }
}
