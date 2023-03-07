package community.independe.controller.form;

import community.independe.domain.post.enums.IndependentPostType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndependentPostForm {

    @NotEmpty
    private String title;
    private String content;
    private IndependentPostType independentPostType;
}
