package community.independe.domain.comment;

import community.independe.domain.BaseEntity;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "text") // 텍스트 타입
    private String content;

    //== 계층형 댓글 ==//
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> child = new ArrayList<>();


    //== 연관 관계 ==//
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 댓글, 회원 N : 1 다대일 단방향 매핑

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post; // 댓글, 게시 N : 1 다대일 단방향 매핑

    //== 생성 ==//
    @Builder
    public Comment(String content, Member member, Post post, Comment parent) {
        this.content = content;
        this.member = member;
        this.post = post;
        post.getComments().add(this);
        if(parent != null) {
            this.parent = parent;
            parent.getChild().add(this);
        }
    }
}
