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
public class RecommendPost extends BaseManyToManyEntity{

    @Id
    @GeneratedValue
    @Column(name = "recommend_post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean isRecommend;

    @Builder
    public RecommendPost(Member member, Post post, Boolean isRecommend) {
        this.member = member;
        addPost(post);
        this.isRecommend = isRecommend;
    }

    public void updateIsRecommend() {
        if (this.isRecommend) {
            this.isRecommend = false;
        } else {
            this.isRecommend = true;
        }
    }

    // 연관관계 메서드
    public void addPost(Post post) {
        this.post = post;
        post.getRecommendPosts().add(this);
    }
}
