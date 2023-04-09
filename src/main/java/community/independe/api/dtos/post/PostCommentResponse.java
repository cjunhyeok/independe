package community.independe.api.dtos.post;

import community.independe.domain.comment.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostCommentResponse {

    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime createdDate;
    private Integer recommendCount;
    private Long parentId;

    public PostCommentResponse(Comment comment){
        this.commentId = comment.getId();
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedDate();
        this.recommendCount = comment.getRecommendCount();
        if (comment.getParent() == null) {
            this.parentId = null;
        } else {
            this.parentId = comment.getParent().getId();
        }
    }
}
