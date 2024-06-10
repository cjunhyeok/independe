package community.independe.service.dtos.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindAllPostsDto {

    private String condition;
    private String keyword;
    private Integer page;
    private Integer size;

    @Builder
    public FindAllPostsDto(String condition, String keyword, Integer page, Integer size) {
        this.condition = condition;
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }
}
