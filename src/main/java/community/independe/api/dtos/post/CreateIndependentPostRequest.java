package community.independe.api.dtos.post;

import community.independe.domain.post.enums.IndependentPostType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIndependentPostRequest {

    private Long memberId;
    @NotEmpty
    private String title;
    private String content;
    @NotEmpty
    private IndependentPostType independentPostType;

}
