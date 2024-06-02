package community.independe.service.dtos.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindIndependentPostRequest {
    private String condition;
    private String keyword;
    private Integer page;
    private Integer size;

    @Builder
    public FindIndependentPostRequest(String condition, String keyword, Integer page, Integer size) {
        this.condition = condition;
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }

    public static FindIndependentPostsDto requestToFindDto(FindIndependentPostRequest findIndependentPostRequest) {
        return FindIndependentPostsDto
                .builder()
                .condition(findIndependentPostRequest.getCondition())
                .keyword(findIndependentPostRequest.getKeyword())
                .page(findIndependentPostRequest.getPage())
                .size(findIndependentPostRequest.getSize())
                .build();
    }
}
