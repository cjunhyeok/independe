package community.independe.service.dtos.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindAllPostsRequest {

    private String condition;
    private String keyword;
    private Integer page;
    private Integer size;

    @Builder
    public FindAllPostsRequest(String condition, String keyword, Integer page, Integer size) {
        this.condition = condition;
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }

    public static FindAllPostsDto requestToFindDto(FindAllPostsRequest findAllPostsRequest) {
        return FindAllPostsDto
                .builder()
                .condition(findAllPostsRequest.getCondition())
                .keyword(findAllPostsRequest.getKeyword())
                .page(findAllPostsRequest.getPage())
                .size(findAllPostsRequest.getSize())
                .build();
    }
}
