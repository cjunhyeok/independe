package community.independe.api.dtos.post;

import community.independe.domain.post.enums.IndependentPostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIndependentPostRequest {

    private Long memberId;
    private String title;
    private String content;
    private IndependentPostType independentPostType;

}
