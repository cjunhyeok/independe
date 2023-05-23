package community.independe.repository.manytomany;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class FavoritePostRepositoryTest {

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
        assertThat(savedFavoritePost).isEqualTo(favoritePost);
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
}
