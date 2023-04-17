package community.independe.api.dtos.post;

import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Slf4j
public class PostResponse {

    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createdDate;
    private String independentPostType;
    private String regionType;
    private String regionPostType;
    private IndependentPostType independentPostTypeEn;
    private RegionType regionTypeEn;
    private RegionPostType regionPostTypeEn;
    private Integer views;
    private Long recommendCount;
    private Long commentCount;
    private Boolean isRecommend;
    private Boolean isFavorite;
    private Boolean isReport;
    private BestCommentDto bestComment;
    private List<PostCommentResponse> comments;

    public PostResponse(Post post, BestCommentDto bestComment ,List<PostCommentResponse> comments, Long commentCount, Long recommendCount, Boolean isRecommend, Boolean isFavorite, Boolean isReport) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getMember().getNickname();
        this.createdDate = post.getCreatedDate();
        this.independentPostType = (post.getIndependentPostType() == null) ? null : post.getIndependentPostType().getDescription();
        this.regionType = (post.getRegionType() == null) ? null : post.getRegionType().getDescription();
        this.regionPostType = (post.getRegionPostType() == null) ? null : post.getRegionPostType().getDescription();
        this.independentPostTypeEn = post.getIndependentPostType();
        this.regionTypeEn = post.getRegionType();
        this.regionPostTypeEn = post.getRegionPostType();
        this.views = post.getViews();
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.isRecommend = isRecommend;
        this.isFavorite = isFavorite;
        this.isReport = isReport;
        this.bestComment = bestComment;
        this.comments = comments;
    }

}
