package community.independe.api.dtos.post;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.*;

@Getter
@NoArgsConstructor
public class SearchResponse {

    private Long postId;
    private String title;
    private String nickname;
    private String independentPostType;
    private String regionType;
    private String regionPostType;
    private IndependentPostType independentPostTypeEn;
    private RegionType regionTypeEn;
    private RegionPostType regionPostTypeEn;
    private int views;
    private Long recommendCount;
    private Long commentCount;
    private boolean isPicture;
    private Long totalCount;

    @Builder
    public SearchResponse(Long postId, String title, String nickname, String independentPostType, String regionType, String regionPostType, IndependentPostType independentPostTypeEn, RegionType regionTypeEn, RegionPostType regionPostTypeEn, int views, Long recommendCount, Long commentCount, boolean isPicture, Long totalCount) {
        this.postId = postId;
        this.title = title;
        this.nickname = nickname;
        this.independentPostType = independentPostType;
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.independentPostTypeEn = independentPostTypeEn;
        this.regionTypeEn = regionTypeEn;
        this.regionPostTypeEn = regionPostTypeEn;
        this.views = views;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.isPicture = isPicture;
        this.totalCount = totalCount;
    }
}
