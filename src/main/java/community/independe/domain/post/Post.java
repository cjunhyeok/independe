package community.independe.domain.post;

import community.independe.domain.BaseEntity;
import community.independe.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED) // 조인 전략
@DiscriminatorColumn
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private Integer views; // 조회수
    private Integer recommendCount; // 추천수
    @Column(columnDefinition = "text") // 텍스트 타입
    private String content;

    //== 연관 관계 ==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 게시글, 회원 N : 1 다대일 단방향 매핑

    public Post(String title, String content, Integer views, Integer recommendCount, Member member) {
        this.title = title;
        this.content = content;
        this.views = views;
        this.recommendCount = recommendCount;
        this.member = member;
    }
}
