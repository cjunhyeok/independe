package community.independe.api.dtos.post;

import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRegionPostRequest {

    private Long memberId;
    private String title;
    private String content;
    private RegionType regionType;
    private RegionPostType regionPostType;
}
