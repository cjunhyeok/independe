package community.independe.service.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.exception.notfound.PostNotFountException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void saveTest() {
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
    void savePostFailTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> favoritePostService.save(postId, memberId))
                .isInstanceOf(PostNotFountException.class)
                .hasMessage("Post Not Exist");

        // then
        verify(postRepository, times(1)).findById(postId);
        verifyNoInteractions(memberRepository);
        verifyNoInteractions(favoritePostRepository);
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
        assertThatThrownBy(() -> favoritePostService.save(postId, memberId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(memberRepository, times(1)).findById(memberId);
        verifyNoInteractions(favoritePostRepository);
    }

    @Test
    void updateIsFavoriteTest() {
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
    void updateIsFavoriteFailTest() {
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

    @Test
    void findByPostIdAndMemberIdTest() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        FavoritePost mockFavoritePost = FavoritePost.builder().build();

        // stub
        when(favoritePostRepository.findByPostIdAndMemberId(postId, memberId)).thenReturn(mockFavoritePost);

        // when
        FavoritePost findFavoritePost = favoritePostService.findByPostIdAndMemberId(postId, memberId);

        // then
        assertThat(findFavoritePost).isEqualTo(mockFavoritePost);
        verify(favoritePostRepository, times(1)).findByPostIdAndMemberId(postId, memberId);
    }

    @Test
    void findByPostIdAndMemberIdAndIsRecommendTest() {
        // when
        Long postId = 1L;
        Long memberId = 1L;
        FavoritePost mockFavoritePost = FavoritePost.builder().isFavorite(true).build();

        // stub
        when(favoritePostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId)).thenReturn(mockFavoritePost);

        // when
        FavoritePost findFavoritePost = favoritePostService.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);

        // then
        assertThat(findFavoritePost).isEqualTo(mockFavoritePost);
        verify(favoritePostRepository, times(1)).findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
    }
}
