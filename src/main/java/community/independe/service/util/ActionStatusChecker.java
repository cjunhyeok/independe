package community.independe.service.util;

import community.independe.domain.manytomany.FavoritePost;
import community.independe.domain.manytomany.RecommendComment;
import community.independe.domain.manytomany.RecommendPost;
import community.independe.domain.manytomany.ReportPost;
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
            RecommendComment recommendComment = recommendCommentRepository.findByCommentIdAndPostIdAndMemberIdAndIsRecommend(commentId, postId, memberId);
            if (recommendComment == null) {
                return false;
            } else {
                return recommendComment.getIsRecommend();
            }
        }
    }

    public boolean isRecommend(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            RecommendPost recommendPost = recommendPostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
            if(recommendPost == null) {
                return false;
            } else {
                return recommendPost.getIsRecommend();
            }
        }
    }

    public boolean isFavorite(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            FavoritePost favoritePost = favoritePostRepository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
            if(favoritePost == null) {
                return false;
            } else {
                return favoritePost.getIsFavorite();
            }
        }
    }

    public boolean isReport(Long postId, Long memberId) {
        if(memberId == null) {
            return false;
        } else {
            ReportPost reportPost = repository.findByPostIdAndMemberIdAndIsRecommend(postId, memberId);
            if(reportPost == null) {
                return false;
            } else {
                return reportPost.getIsReport();
            }
        }
    }
}
