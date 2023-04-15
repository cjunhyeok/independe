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
public class ReportPost extends BaseManyToManyEntity{

    @Id
    @GeneratedValue
    @Column(name = "report_post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean isReport;

    @Builder
    public ReportPost(Member member, Post post, Boolean isReport) {
        this.member = member;
        this.post = post;
        this.isReport = isReport;
    }

    public void updateIsReport(Boolean isReport) {
        this.isReport = isReport;
    }
}
