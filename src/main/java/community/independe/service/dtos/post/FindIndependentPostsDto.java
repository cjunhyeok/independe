package community.independe.service.dtos.post;

import community.independe.domain.post.enums.IndependentPostType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindIndependentPostsDto {

    private IndependentPostType independentPostType;
    private String condition;
    private String keyword;
    private Integer page;
    private Integer size;

    @Builder
    public FindIndependentPostsDto(IndependentPostType independentPostType, String condition, String keyword, Integer page, Integer size) {
        this.independentPostType = independentPostType;
        this.condition = condition;
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }
}
