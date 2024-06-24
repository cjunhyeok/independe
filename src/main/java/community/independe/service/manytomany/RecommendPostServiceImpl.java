package community.independe.service.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendPostServiceImpl implements RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public Long save(Long postId, Long memberId) {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        RecommendPost findRecommendPost = recommendPostRepository.findByPostIdAndMemberId(postId, memberId);

        if (findRecommendPost == null) {
            RecommendPost savedRecommendPost = recommendPostRepository.save(
                    RecommendPost.builder()
                            .member(findMember)
                            .post(findPost)
                            .isRecommend(true)
                            .build()
            );

            return savedRecommendPost.getId();
        } else {
            findRecommendPost.updateIsRecommend();
            return findRecommendPost.getId();
        }
    }

    @Override
    public Long countByPostId(Long postId) {
        return recommendPostRepository.countAllByPostIdAndIsRecommend(postId);
    }
}
