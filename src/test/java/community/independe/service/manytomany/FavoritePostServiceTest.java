package community.independe.service.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoritePostServiceTest {

    @InjectMocks
    private FavoritePostServiceImpl favoritePostService;
    @Mock
    private FavoritePostRepository favoritePostRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    public void saveTest() {
        // given
        Long memberId = 1L;
        Long postId = 1L;

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(favoritePostRepository.save(any(FavoritePost.class))).thenReturn(FavoritePost.builder().build());

        // when
        Long savedFavoritePostService = favoritePostService.save(postId, memberId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(favoritePostRepository, times(1)).save(any(FavoritePost.class));
    }

    @Test
    public void updateIsFavoriteTest() {
        // given
        FavoritePost mockFavoritePost = FavoritePost.builder()
                .isFavorite(false)
                .build();
        boolean isFavorite = true;

        // stub
        when(favoritePostRepository.findById(mockFavoritePost.getId())).thenReturn(Optional.of(mockFavoritePost));

        // when
        favoritePostService.updateIsFavorite(mockFavoritePost, isFavorite);

        // then
        assertThat(mockFavoritePost.getIsFavorite()).isTrue();
    }

    @Test
    public void updateIsFavoriteFailTest() {
        // given
        FavoritePost mockFavoritePost = FavoritePost.builder()
                .isFavorite(false)
                .build();
        boolean isFavorite = true;

        // stub
        when(favoritePostRepository.findById(mockFavoritePost.getId())).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> favoritePostService.updateIsFavorite(mockFavoritePost, isFavorite));

        // then
        verify(favoritePostRepository, times(1)).findById(mockFavoritePost.getId());
        assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
