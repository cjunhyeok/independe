package community.independe.api.android.dto;

import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AndroidCreateRegionPostRequest {

    private String title;
    private String content;
    private RegionType regionType;
    private RegionPostType regionPostType;
}
