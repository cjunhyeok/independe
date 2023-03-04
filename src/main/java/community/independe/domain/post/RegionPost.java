package community.independe.domain.post;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.domain.post.enums.RegionPostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegionPost extends Post{

    @Enumerated(EnumType.STRING)
    private RegionType regionType;

    @Enumerated(EnumType.STRING)
    private RegionPostType regionPostType;

    @Builder
    public RegionPost(String title, String content, Member member, RegionType regionType, RegionPostType regionPostType) {
        super(title, content, member);
        this.regionType = regionType;
        this.regionPostType = regionPostType;
    }
}
