package community.independe.domain.post;

import community.independe.domain.enums.IndependentPostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class IndependentPost extends Post{

    @Enumerated(EnumType.STRING)
    private IndependentPostType independentPostType;
}
