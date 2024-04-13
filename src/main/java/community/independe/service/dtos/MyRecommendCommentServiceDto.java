package community.independe.service.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyRecommendCommentServiceDto {

    private Long postId;
    private Long commentId;
    private String content;
    private LocalDateTime createdDate;
    private Long totalCount;

    @Builder
    public MyRecommendCommentServiceDto(Long postId, Long commentId, String content, LocalDateTime createdDate, Long totalCount) {
        this.postId = postId;
        this.commentId = commentId;
        this.content = content;
        this.createdDate = createdDate;
        this.totalCount = totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
