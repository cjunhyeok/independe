package community.independe.service.integration.manytomany;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.ReportPostRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.manytomany.ReportPostService;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class ReportPostServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private ReportPostService reportPostService;
    @Autowired
    private ReportPostRepository reportPostRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 신고를 저장한다.")
    void saveTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);

        // when
        Long savedReportPostId = reportPostService.save(savedPost.getId(), savedMember.getId());

        // then
        ReportPost findReportPost = reportPostRepository.findById(savedReportPostId).get();
        assertThat(findReportPost.getId()).isEqualTo(savedReportPostId);
        assertThat(findReportPost.getMember()).isEqualTo(savedMember);
        assertThat(findReportPost.getPost()).isEqualTo(savedPost);
        assertThat(findReportPost.getIsReport()).isTrue();
    }

    @Test
    @DisplayName("게시글 신고 저장 시 회원 PK 를 잘못 입력하면 예외가 발생한다.")
    void saveMemberFailTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> reportPostService.save(savedPost.getId(), savedMember.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }
    
    @Test
    @DisplayName("게시글 추천 시 게시글 PK 를 잘못 입력하면 예외가 발생한다.")
    void PostFailTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> reportPostService.save(savedPost.getId() + 1L, savedMember.getId()))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글 신고 저장 후 게시글 신고 시 신고 여부가 false 가 된다.")
    void saveIsReportFalseTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);
        reportPostService.save(savedPost.getId(), savedMember.getId());

        // when
        Long savedReportPostId = reportPostService.save(savedPost.getId(), savedMember.getId());

        // then
        ReportPost findReportPost = reportPostRepository.findById(savedReportPostId).get();
        assertThat(findReportPost.getId()).isEqualTo(savedReportPostId);
        assertThat(findReportPost.getMember()).isEqualTo(savedMember);
        assertThat(findReportPost.getPost()).isEqualTo(savedPost);
        assertThat(findReportPost.getIsReport()).isFalse();
    }

    @Test
    @DisplayName("게시글 신고 저장 후 게시글 신고 시 신고 여부가 false 이면 true 가 된다.")
    void saveIsReportTrueTest() {
        // given
        Member savedMember = createMember("username", "nickname");
        Post savedPost = createPost(savedMember);
        reportPostService.save(savedPost.getId(), savedMember.getId());
        reportPostService.save(savedPost.getId(), savedMember.getId());

        // when
        Long savedReportPostId = reportPostService.save(savedPost.getId(), savedMember.getId());

        // then
        ReportPost findReportPost = reportPostRepository.findById(savedReportPostId).get();
        assertThat(findReportPost.getId()).isEqualTo(savedReportPostId);
        assertThat(findReportPost.getMember()).isEqualTo(savedMember);
        assertThat(findReportPost.getPost()).isEqualTo(savedPost);
        assertThat(findReportPost.getIsReport()).isTrue();
    }

    private Member createMember(String username, String nickname) {
        Member member = Member.builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .build();
        return memberRepository.save(member);
    }

    private Post createPost(Member member) {
        Post post = Post
                .builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(member)
                .build();
        return postRepository.save(post);
    }
}
