package community.independe.domain.post;

import community.independe.domain.post.enums.RegionType;
import community.independe.domain.post.enums.RegionPostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class RegionPost extends Post{

    @Enumerated(EnumType.STRING)
    private RegionType regionType;

    @Enumerated(EnumType.STRING)
    private RegionPostType regionPostType;
}
