package community.independe.api.dtos.video;

import community.independe.domain.post.enums.IndependentPostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindVideosResponse {

    private String videoUrl;
    private String videoMasterUrl;
    private String masterName;
    private String videoTitle;
    private IndependentPostType independentPostType; // 카테고리
    private int views;
}
