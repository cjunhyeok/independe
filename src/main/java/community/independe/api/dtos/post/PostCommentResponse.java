package community.independe.api.dtos.post;

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
    private Long recommendCount;
    private Long parentId;
    private Boolean isRecommend;

    public PostCommentResponse(Long commentId, String nickname, String content, LocalDateTime createdDate, Long recommendCount, Long parentId, Boolean isRecommend) {
        this.commentId = commentId;
        this.nickname = nickname;
        this.content = content;
        this.createdDate = createdDate;
        this.recommendCount = recommendCount;
        if (parentId == null) {
            this.parentId = null;
        } else {
            this.parentId = parentId;
        }
        this.isRecommend = isRecommend;
    }
}
