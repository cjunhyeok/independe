package community.independe.api.dtos.post;

import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRegionPostRequest {

    private Long memberId;
    @NotEmpty
    private String title;
    private String content;
    @NotEmpty
    private RegionType regionType;
    @NotEmpty
    private RegionPostType regionPostType;
}
