package community.independe.service.manytomany;

import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.repository.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendCommentServiceImpl implements RecommendCommentService{

    private final RecommendCommentRepository recommendCommentRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long save(Long commentId, Long memberId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not exist"));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not exist"));

        RecommendComment savedRecommendComment = recommendCommentRepository.save(
                RecommendComment.builder()
                        .comment(findComment)
                        .member(findMember)
                        .isRecommend(true)
                        .build()
        );

        return savedRecommendComment.getId();
    }

    @Override
    public RecommendComment findByCommentIdAndMemberId(Long commentId, Long memberId) {
        return recommendCommentRepository.findByCommentIdAndMemberId(commentId, memberId);
    }

    @Override
    @Transactional
    public void updateIsRecommend(RecommendComment recommendComment, Boolean isRecommend) {
        recommendComment.updateIsRecommend(isRecommend);
    }
}
