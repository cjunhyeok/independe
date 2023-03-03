package community.independe.domain.comment;

import community.independe.domain.BaseEntity;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "text") // 텍스트 타입
    private String content;

    //== 연관 관계 ==//
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 댓글, 회원 N : 1 다대일 단방향 매핑

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post; // 댓글, 게시 N : 1 다대일 단방향 매핑
}
