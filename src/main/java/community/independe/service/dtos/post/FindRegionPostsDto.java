package community.independe.service.dtos.post;

import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindRegionPostsDto {

    private RegionType regionType;
    private RegionPostType regionPostType;
    private String condition;
    private String keyword;
    private Integer page;
    private Integer size;

    @Builder
    public FindRegionPostsDto(RegionType regionType, RegionPostType regionPostType, String condition, String keyword, Integer page, Integer size) {
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.condition = condition;
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }
}
