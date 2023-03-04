package community.independe.domain.post;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
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
public class IndependentPost extends Post{

    @Enumerated(EnumType.STRING)
    private IndependentPostType independentPostType;

    @Builder
    public IndependentPost(String title, String content, Member member, IndependentPostType independentPostType) {
        super(title, content, member);
        this.independentPostType = independentPostType;
    }
}
