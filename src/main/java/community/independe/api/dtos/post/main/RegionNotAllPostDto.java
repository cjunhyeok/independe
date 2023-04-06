package community.independe.api.dtos.post.main;

import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionNotAllPostDto {

    private Long postId;
    private String title;
    private String regionType;
    private String regionPostType;
    private RegionType regionTypeEn;
    private RegionPostType regionPostTypeEn;
    private int recommendCount;
    private Long commentCount;
    private boolean isPicture;
}
