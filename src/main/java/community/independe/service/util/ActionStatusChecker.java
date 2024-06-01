package community.independe.service.util;

import community.independe.repository.manytomany.FavoritePostRepository;
import community.independe.repository.manytomany.RecommendCommentRepository;
import community.independe.repository.manytomany.RecommendPostRepository;
import community.independe.repository.manytomany.ReportPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActionStatusChecker {

    private final RecommendCommentRepository recommendCommentRepository;
    private final RecommendPostRepository recommendPostRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final ReportPostRepository repository;

    public boolean isRecommendComment(Long commentId, Long postId, Long memberId) {
        if (memberId == null) {
            return false;
        } else {
            if (recommendCommentRepository.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean isRecommend(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            if(recommendPostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean isFavorite(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            if(favoritePostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean isReport(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            if(repository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId) == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
