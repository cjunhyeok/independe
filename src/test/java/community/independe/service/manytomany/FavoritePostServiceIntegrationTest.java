package community.independe.service.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.manytomany.dtos.GetFavoritePostServiceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
