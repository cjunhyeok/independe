package community.independe.service.dtos;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyPostServiceDto {

    private Long postId;
    private String title;
    private IndependentPostType independentPostType;
    private RegionType regionType;
    private RegionPostType regionPostType;
    private LocalDateTime createdDate;

    @Builder
    public MyPostServiceDto(Long postId, String title, IndependentPostType independentPostType, RegionType regionType, RegionPostType regionPostType, LocalDateTime createdDate) {
        this.postId = postId;
        this.title = title;
        this.independentPostType = independentPostType;
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.createdDate = createdDate;
    }
}
