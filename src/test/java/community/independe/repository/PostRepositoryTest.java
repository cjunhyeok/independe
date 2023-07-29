package community.independe.repository;

import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.post.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void initData() {
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        Post independentPost = Post.builder()
                .title("independentTitle")
                .content("independentContent")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post independentPost2 = Post.builder()
                .title("independentTitle2")
                .content("independentContent2")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post independentPost3 = Post.builder()
                .title("independentTitle3")
                .content("independentContent3")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();

        Post regionPost = Post.builder()
                .title("regionTitle")
                .content("regionContent")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(savedMember)
                .build();
        Post regionPost2 = Post.builder()
                .title("regionTitle2")
                .content("regionContent2")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(savedMember)
                .build();

        Post savedIndependentPost = postRepository.save(independentPost);
        Post savedIndependentPost2 = postRepository.save(independentPost2);
        Post savedIndependentPost3 = postRepository.save(independentPost3);
        Post savedRegionPost = postRepository.save(regionPost);
        Post savedRegionPost2 = postRepository.save(regionPost2);
    }

    @Test
    public void saveTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        Member savedMember = memberRepository.save(member);

        Post independentPost = Post.builder()
                .title("saveIndependentTitle")
                .content("saveIndependentContent")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();

        Post regionPost = Post.builder()
                .title("saveRegionTitle")
                .content("saveRegionContent")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(savedMember)
                .build();

        // when
        Post savedIndependentPost = postRepository.save(independentPost);
        Post savedRegionPost = postRepository.save(regionPost);

        //then
        assertThat(savedIndependentPost.getTitle()).isEqualTo("saveIndependentTitle");
        assertThat(savedRegionPost.getTitle()).isEqualTo("saveRegionTitle");
    }

    @Test
    void findAllRegionPostsByTypesWithMemberDynamicTest() {
        // given
        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;
        String condition = "";
        String keyword = "";
        PageRequest page = PageRequest.of(0, 5);

        // when
        Page<Post> allRegionPostsByTypesWithMemberDynamic = postRepository.findAllRegionPostsByTypesWithMemberDynamic(regionType, regionPostType, condition, keyword, page);
        List<Post> content = allRegionPostsByTypesWithMemberDynamic.getContent();

        // then
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(1).getTitle()).isEqualTo("regionTitle");
        assertThat(content.get(1).getContent()).isEqualTo("regionContent");
        assertThat(content.get(0).getTitle()).isEqualTo("regionTitle2");
        assertThat(content.get(0).getContent()).isEqualTo("regionContent2");
    }

    @Test
    void findAllRegionPostsByTypesWithMemberDynamicSearchTest() {
        // given
        Member findMember = memberRepository.findByUsername("id");
        Post regionPost = Post.builder()
                .title("SearchTitle")
                .content("SearchContent")
                .regionType(RegionType.ALL)
                .regionPostType(RegionPostType.FREE)
                .member(findMember)
                .build();

        postRepository.save(regionPost);

        RegionType regionType = RegionType.ALL;
        RegionPostType regionPostType = RegionPostType.FREE;
        String condition = "title";
        String keyword = "Search";
        PageRequest page = PageRequest.of(0, 5);

        // when
        Page<Post> allRegionPostsByTypesWithMemberDynamic = postRepository.findAllRegionPostsByTypesWithMemberDynamic(regionType, regionPostType, condition, keyword, page);
        List<Post> content = allRegionPostsByTypesWithMemberDynamic.getContent();

        // then
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getTitle()).isEqualTo("SearchTitle");
        assertThat(content.get(0).getContent()).isEqualTo("SearchContent");
    }
}
