package community.independe.repository.manytomany;

import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ReportPostRepositoryTest {

    @Autowired
    private ReportPostRepository reportPostRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    void saveTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().member(member).build();
        Post savedPost = postRepository.save(post);
        ReportPost reportPost =
                ReportPost.builder().member(savedMember).post(savedPost).isReport(true).build();

        // when
        ReportPost savedReportPost = reportPostRepository.save(reportPost);

        // then
        assertThat(savedReportPost).isEqualTo(reportPost);
    }

    @Test
    void findByPostIdAndMemberIdTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().member(member).build();
        Post savedPost = postRepository.save(post);
        ReportPost reportPost =
                ReportPost.builder().member(savedMember).post(savedPost).isReport(false).build();
        ReportPost savedReportPost = reportPostRepository.save(reportPost);

        // when
        ReportPost findReportPost =
                reportPostRepository.findByPostIdAndMemberId(savedMember.getId(), savedPost.getId());

        // then
        assertThat(findReportPost).isEqualTo(savedReportPost);
        assertThat(findReportPost.getId()).isEqualTo(savedReportPost.getId());
        assertThat(findReportPost.getIsReport()).isEqualTo(savedReportPost.getIsReport());
        assertThat(findReportPost.getPost()).isEqualTo(savedReportPost.getPost());
        assertThat(findReportPost.getMember()).isEqualTo(savedReportPost.getMember());
        assertThat(findReportPost.getCreatedDate()).isNotNull();
    }

    @Test
    void findByPostIdAndMemberIdAndIsRecommendTest() {
        // given
        Member member = Member.builder().build();
        Member savedMember = memberRepository.save(member);
        Post post = Post.builder().member(member).build();
        Post savedPost = postRepository.save(post);
        ReportPost reportPost =
                ReportPost.builder().member(savedMember).post(savedPost).isReport(true).build();
        ReportPost savedReportPost = reportPostRepository.save(reportPost);

        // when
        ReportPost findReportPost =
                reportPostRepository.findByPostIdAndMemberIdAndIsRecommend(savedMember.getId(), savedPost.getId());

        // then
        assertThat(findReportPost).isEqualTo(savedReportPost);
    }
}
