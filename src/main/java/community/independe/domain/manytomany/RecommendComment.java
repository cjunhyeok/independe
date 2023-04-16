package community.independe.domain.manytomany;

import community.independe.domain.comment.Comment;
import community.independe.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendComment {

    @Id
    @GeneratedValue
    @Column(name = "recommend_comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private Boolean isRecommend;

    @Builder
    public RecommendComment(Member member, Comment comment, Boolean isRecommend) {
        this.member = member;
        this.comment = comment;
        this.isRecommend = isRecommend;
    }

    public void updateIsRecommend(Boolean isRecommend) {
        this.isRecommend = isRecommend;
    }
}
