package community.independe.service.manytomany;

import community.independe.domain.manytomany.ReportPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.manytomany.ReportPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportPostServiceImpl implements ReportPostService {

    private final ReportPostRepository reportPostRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Long save(Long postId, Long memberId) {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

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
    @Transactional
    public void updateIsReport(ReportPost reportPost, Boolean isRecommend) {
        reportPost.updateIsReport(isRecommend);
    }

    @Override
    public ReportPost findByPostIdAndMemberId(Long postId, Long memberId) {
        return reportPostRepository.findByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public ReportPost findByPostIdAndMemberIdAndIsRecommend(Long postId, Long memberId) {
        return reportPostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
    }
}
