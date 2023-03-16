package community.independe.api.dtos.post.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionAllPostDto {

    private Long postId;
    private String title;
    private int recommendCount;
    private Long commentCount;
    private boolean isPicture;
}
