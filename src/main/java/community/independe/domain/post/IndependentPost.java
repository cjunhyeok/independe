package community.independe.domain.post;

import community.independe.domain.post.enums.IndependentPostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Entity
@Getter
public class IndependentPost extends Post{

    @Enumerated(EnumType.STRING)
    private IndependentPostType independentPostType;
}
