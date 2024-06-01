package community.independe.service.manytomany;

import community.independe.api.dtos.post.BestCommentDto;
import community.independe.domain.comment.Comment;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.MemberRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendCommentServiceImpl implements RecommendCommentService{

    private final RecommendCommentRepository recommendCommentRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long save(Long commentId, Long memberId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

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

    @Override
    public Long countAllByCommentIdAndIsRecommend(Long commentId) {
        return recommendCommentRepository.countAllByCommentIdAndIsRecommend(commentId);
    }

    @Override
    public RecommendComment findByCommentIdAndPostIdAndMemberIdAndIsRecommend(Long commentId, Long postId, Long memberId) {
        return recommendCommentRepository.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId);
    }

    @Override
    public BestCommentDto findBestComment() {
        List<Object[]> bestCommentList = recommendCommentRepository.findBestComment();
        BestCommentDto bestCommentDto = null;

        if (bestCommentList.isEmpty()) {
            bestCommentDto = null;
        } else {
            Object[] bestCommentObject = bestCommentList.get(0);
            Comment bestComment = (Comment) bestCommentObject[0];
            Long bestCommentRecommendCount = (Long) bestCommentObject[1];
            bestCommentDto = new BestCommentDto(
                    bestComment.getId(),
                    bestComment.getMember().getNickname(),
                    bestComment.getContent(),
                    bestComment.getCreatedDate(),
                    bestCommentRecommendCount
            );
        }

        return bestCommentDto;
    }
}
