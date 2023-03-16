package community.independe.api.dtos.post.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionNotAllPostDto {

    private String title;
    private String regionType;
    private String regionPostType;
    private int recommendCount;
    private Long commentCount;
    private boolean isPicture;
}
