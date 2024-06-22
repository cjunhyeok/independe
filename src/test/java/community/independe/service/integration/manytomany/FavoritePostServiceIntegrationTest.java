package community.independe.service.integration.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.manytomany.FavoritePostService;
import community.independe.service.manytomany.dtos.GetFavoritePostServiceDto;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class FavoritePostServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FavoritePostRepository favoritePostRepository;
    @Autowired
    private FavoritePostService favoritePostService;

    @Test
    @DisplayName("게시글 즐겨찾기를 조회한다.")
    void saveTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);

        // when
        Long savedFavoritePostId
                = favoritePostService.save(savedPost.getId(), savedMember.getId());

        // then
        FavoritePost findFavoritePost = favoritePostRepository.findById(savedFavoritePostId).get();
        assertThat(findFavoritePost.getId()).isEqualTo(savedFavoritePostId);
        assertThat(findFavoritePost.getPost()).isEqualTo(savedPost);
        assertThat(findFavoritePost.getMember()).isEqualTo(savedMember);
        assertThat(findFavoritePost.getIsFavorite()).isTrue();
    }

    @Test
    @DisplayName("게시글 즐겨찾기 저장 시 회원 PK 를 잘못 입력하면 예외가 발생한다.")
    void saveMemberFailTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> favoritePostService.save(savedPost.getId(), savedMember.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 즐겨찾기 저장 시 게시글 PK 를 잘못 입력하면 예외가 발생한다.")
    void savePostFailTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> favoritePostService.save(savedPost.getId() + 1L, savedMember.getId()))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 즐겨찾기 저장 후 즐겨찾기 시 즐겨찾기 여부가 false 가 된다.")
    void saveIsFavoriteFalseTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        favoritePostService.save(savedPost.getId(), savedMember.getId());

        // when
        Long savedFavoritePostId = favoritePostService.save(savedPost.getId(), savedMember.getId());

        // then
        FavoritePost findFavoritePost = favoritePostRepository.findById(savedFavoritePostId).get();
        assertThat(findFavoritePost.getIsFavorite()).isFalse();
    }

    @Test
    @DisplayName("게시글 즐겨찾기 저장 후 즐겨찾기 시 즐겨찾기 여부가 false 이면 true 가 된다")
    void saveIsFavoriteTrueTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = createPost(savedMember);
        favoritePostService.save(savedPost.getId(), savedMember.getId());
        favoritePostService.save(savedPost.getId(), savedMember.getId());

        // when
        Long savedFavoritePostId = favoritePostService.save(savedPost.getId(), savedMember.getId());

        // then
        FavoritePost findFavoritePost = favoritePostRepository.findById(savedFavoritePostId).get();
        assertThat(findFavoritePost.getIsFavorite()).isTrue();
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

    @Test
    @DisplayName("회원 PK로 즐겨찾기 목록 DTO 를 조회한다.")
    void findFavoritePostByMemberIdTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        FavoritePost favoritePost = FavoritePost.builder()
                .isFavorite(true)
                .post(savedPost)
                .member(savedMember)
                .build();
        FavoritePost savedFavoritePost = favoritePostRepository.save(favoritePost);

        Post post2 = Post.builder()
                .title("title2")
                .content("content2")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost2 = postRepository.save(post2);

        FavoritePost favoritePost2 = FavoritePost.builder()
                .isFavorite(true)
                .post(savedPost2)
                .member(savedMember)
                .build();
        FavoritePost savedFavoritePost2 = favoritePostRepository.save(favoritePost2);

        // when
        List<GetFavoritePostServiceDto> serviceDto = favoritePostService.findFavoritePostByMemberId(savedMember.getId(), 0, 10);

        // then
        assertThat(serviceDto).hasSize(2);
        assertThat(serviceDto.get(0).getTotalCount()).isEqualTo(2);
        assertThat(serviceDto.get(0).getNickname()).isNotNull();
    }
}
