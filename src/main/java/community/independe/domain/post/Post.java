package community.independe.domain.post;

import community.independe.domain.BaseEntity;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private int views; // 조회수

    @Column(columnDefinition = "text") // 텍스트 타입
    private String content;

    @Enumerated(EnumType.STRING)
    private IndependentPostType independentPostType; // 자취 게시판 카테고리

    @Enumerated(EnumType.STRING)
    private RegionType regionType; // 지역 게시판 지역 정보

    @Enumerated(EnumType.STRING)
    private RegionPostType regionPostType; // 지역 게시판 카테고리

    //== 연관 관계 ==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 게시글, 회원 N : 1 다대일 단방향 매핑

    @Builder
    public Post(String title, String content, IndependentPostType independentPostType, RegionType regionType, RegionPostType regionPostType, Member member) {
        this.title = title;
        this.content = content;
        this.independentPostType = independentPostType;
        this.regionType = regionType;
        this.regionPostType = regionPostType;
        this.member = member;
        this.views = 0;
    }

    // 조회수 증가
    public void increaseViews(int views) {
        this.views = views;
    }
}
