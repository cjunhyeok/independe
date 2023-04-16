package community.independe.api.android.dto;

import community.independe.domain.post.enums.IndependentPostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AndroidCreateIndependentPostRequest {

    private String title;
    private String content;
    private IndependentPostType independentPostType;
}
