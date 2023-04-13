package community.independe.repository;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class FavoritePostRepositoryTest {

    @Autowired
    private FavoritePostRepository favoritePostRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void saveTest() {
        Member member = Member.builder()
                .username("id1")
                .password("Wnsgur1214@")
                .nickname("nick1")
                .role("ROLE_USER")
                .build();
        memberRepository.save(member);

        Post regionPost = Post.builder()
                .title("regionTitle")
                .content("regionContent")
                .member(member)
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .build();
        postRepository.save(regionPost);

        FavoritePost favoritePost = FavoritePost.builder()
                .member(member)
                .post(regionPost)
                .build();

        favoritePostRepository.save(favoritePost);

        FavoritePost findFavoritePost = favoritePostRepository.findById(favoritePost.getId()).get();
        Assertions.assertThat(findFavoritePost.getId()).isEqualTo(favoritePost.getId());
    }
}