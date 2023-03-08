package community.independe.controller.form;

import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionPostForm {

    @NotEmpty
    private String title;
    private String content;
    private RegionType regionType;
    private RegionPostType regionPostType;
}
