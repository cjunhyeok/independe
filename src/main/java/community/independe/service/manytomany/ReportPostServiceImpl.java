package community.independe.service.manytomany;

import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.manytomany.ReportPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {

    private final ReportPostRepository reportPostRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Long save(Long postId, Long memberId) {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not exist"));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not exist"));

        ReportPost savedReportPost = reportPostRepository.save(
                ReportPost.builder().
                        member(findMember)
                        .post(findPost)
                        .isReport(true)
                        .build()
        );

        return savedReportPost.getId();
    }

    @Override
    public ReportPost findByPostIdAndMemberId(Long postId, Long memberId) {
        return reportPostRepository.findByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public ReportPost findByPostIdAndMemberIdAndIsRecommend(Long postId, Long memberId) {
        return reportPostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
    }

    @Override
    @Transactional
    public void updateIsReport(ReportPost reportPost, Boolean isRecommend) {
        reportPost.updateIsReport(isRecommend);
    }
}
