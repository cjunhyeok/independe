package community.independe.api.dtos.manytomany.recommendpost;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FavoritePostResponse {

    private String title;
    private IndependentPostType independentPostType;
    private RegionType regionType;
    private RegionPostType regionPostType;
    private String nickname;
    private LocalDateTime createdDate;

    @Builder
    public FavoritePostResponse(String title, IndependentPostType independentPostType, RegionType regionType, RegionPostType regionPostType, String nickname, LocalDateTime createdDate) {
        this.title = title;
        this.independentPostType = independentPostType;
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.nickname = nickname;
        this.createdDate = createdDate;
    }
}
