package community.independe.api.dtos.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BestCommentDto {

    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime createdDate;
    private Long recommendCount;

    public BestCommentDto(Long commentId, String nickname, String content, LocalDateTime createdDate, Long recommendCount) {
        this.commentId = commentId;
        this.nickname = nickname;
        this.content = content;
        this.createdDate = createdDate;
        this.recommendCount = recommendCount;
    }
}
