package community.independe.service.dtos.post;

import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FindPostDto {

    private Long id;
    private String title;
    private String content;
    private IndependentPostType independentPostType;
    private RegionType regionType;
    private RegionPostType regionPostType;
    private int views;
    private LocalDateTime createdDate;
    private Long memberId;
    private String nickname;

    @Builder
    public FindPostDto(Long id, String title, String content, IndependentPostType independentPostType, RegionType regionType, RegionPostType regionPostType, int views, LocalDateTime createdDate, Long memberId, String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.independentPostType = independentPostType;
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.views = views;
        this.createdDate = createdDate;
        this.memberId = memberId;
        this.nickname = nickname;
    }
}
