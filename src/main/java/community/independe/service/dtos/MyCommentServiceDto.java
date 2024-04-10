package community.independe.service.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyCommentServiceDto {

    private Long postId;
    private Long commentId;
    private String content;
    private LocalDateTime createdDate;

    @Builder
    public MyCommentServiceDto(Long postId, Long commentId, String content, LocalDateTime createdDate) {
        this.postId = postId;
        this.commentId = commentId;
        this.content = content;
        this.createdDate = createdDate;
    }
}
