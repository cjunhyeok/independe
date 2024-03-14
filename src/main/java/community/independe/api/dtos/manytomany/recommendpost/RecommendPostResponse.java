package community.independe.api.dtos.manytomany.recommendpost;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendPostResponse {

    private Long recommendPostCount;

    @Builder
    public RecommendPostResponse(Long recommendPostCount) {
        this.recommendPostCount = recommendPostCount;
    }
}
