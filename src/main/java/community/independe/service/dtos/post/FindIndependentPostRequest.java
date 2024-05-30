package community.independe.service.dtos.post;

import community.independe.domain.post.enums.IndependentPostType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindIndependentPostRequest {
    private IndependentPostType independentPostType;
    private String condition;
    private String keyword;
    private Integer page;
    private Integer size;

    @Builder
    public FindIndependentPostRequest(IndependentPostType independentPostType, String condition, String keyword, Integer page, Integer size) {
        this.independentPostType = independentPostType;
        this.condition = condition;
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }

    public static FindIndependentPostsDto requestToFindDto(FindIndependentPostRequest findIndependentPostRequest) {
        return FindIndependentPostsDto
                .builder()
                .independentPostType(findIndependentPostRequest.independentPostType)
                .condition(findIndependentPostRequest.getCondition())
                .keyword(findIndependentPostRequest.getKeyword())
                .page(findIndependentPostRequest.getPage())
                .size(findIndependentPostRequest.getSize())
                .build();
    }
}
