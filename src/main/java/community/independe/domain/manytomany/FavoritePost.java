package community.independe.domain.manytomany;

import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoritePost extends BaseManyToManyEntity {

    @Id @GeneratedValue
    @Column(name = "favorite_post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean isFavorite;

    @Builder
    public FavoritePost(Member member, Post post) {
        this.member = member;
        this.post = post;
        this.isFavorite = true;
    }
}
