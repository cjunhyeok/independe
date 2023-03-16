package community.independe.api.dtos.post.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularPostDto {

    private String title;
    private String independentPostType;
    private String regionType;
    private String regionPostType;
    private int views;
    private int recommendCount;
    private Long commentCount;
    private boolean isPicture;
}
