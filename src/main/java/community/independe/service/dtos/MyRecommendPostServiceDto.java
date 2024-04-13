package community.independe.service.dtos;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyRecommendPostServiceDto {
    private Long postId;
    private Long memberId;
    private String title;
    private IndependentPostType independentPostType;
    private RegionType regionType;
    private RegionPostType regionPostType;
    private String nickname;
    private LocalDateTime createdDate;
    private Long totalCount;

    @Builder
    public MyRecommendPostServiceDto(Long postId, Long memberId, String title, IndependentPostType independentPostType, RegionType regionType, RegionPostType regionPostType, String nickname, LocalDateTime createdDate) {
        this.postId = postId;
        this.memberId = memberId;
        this.title = title;
        this.independentPostType = independentPostType;
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.nickname = nickname;
        this.createdDate = createdDate;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
