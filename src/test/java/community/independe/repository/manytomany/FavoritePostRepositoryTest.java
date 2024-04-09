package community.independe.repository.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class FavoritePostRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private FavoritePostRepository favoritePostRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void saveTest() {
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

        // when
        FavoritePost savedFavoritePost = favoritePostRepository.save(favoritePost);

        // then
        assertThat(savedFavoritePost.getIsFavorite()).isTrue();
        assertThat(savedFavoritePost.getId()).isEqualTo(favoritePost.getId());
        assertThat(savedFavoritePost).isEqualTo(favoritePost);
        assertThat(savedFavoritePost.getPost()).isEqualTo(favoritePost.getPost());
        assertThat(savedFavoritePost.getMember()).isEqualTo(favoritePost.getMember());
        assertThat(savedFavoritePost.getCreatedDate()).isNotNull();
    }

    @Test
    public void findByPostIdAndMemberIdTest() {
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

        Post nextPost = Post.builder()
                .title("nextTitle")
                .content("content")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post savedNextPost = postRepository.save(nextPost);

        FavoritePost favoritePost = FavoritePost.builder()
                .isFavorite(true)
                .post(savedPost)
                .member(savedMember)
                .build();
        FavoritePost savedFavoritePost = favoritePostRepository.save(favoritePost);


        // when
        FavoritePost findByPostIdAndMemberId =
                favoritePostRepository.findByPostIdAndMemberId(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findByPostIdAndMemberId.getPost()).isEqualTo(savedPost);
    }

    @Test
    public void findByPostIdAndMemberIdAndIsRecommendTest() {
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

        Post nextPost = Post.builder()
                .title("nextTitle")
                .content("content")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post savedNextPost = postRepository.save(nextPost);

        FavoritePost favoritePost = FavoritePost.builder()
                .isFavorite(true)
                .post(savedPost)
                .member(savedMember)
                .build();
        FavoritePost savedFavoritePost = favoritePostRepository.save(favoritePost);


        // when
        FavoritePost findByPostIdAndMemberId =
                favoritePostRepository.findByPostIdAndMemberIdAndIsRecommend(savedPost.getId(), savedMember.getId());

        // then
        assertThat(findByPostIdAndMemberId.getIsFavorite()).isTrue();
    }

    @Test
    @DisplayName("회원 PK로 즐겨찾기 게시글을 조회한다.")
    void findPostByMemberIdTest() {
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
        List<Post> findPosts = favoritePostRepository.findPostByMemberId(member.getId());

        // then
        assertThat(findPosts).hasSize(2);
    }
}
