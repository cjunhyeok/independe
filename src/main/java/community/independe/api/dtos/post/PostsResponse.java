package community.independe.api.dtos.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostsResponse {

    private Long postId;
    private String nickName;
    private String title;
    private LocalDateTime createdDate;
    private Integer views; // 조회수
    private Long recommendCount; // 추천수
    private Long commentCount; // 댓글수
    private boolean isPicture;
    private Long totalCount;

    @Builder
    public PostsResponse(Long postId, String nickName, String title, LocalDateTime createdDate, Integer views, Long recommendCount, Long commentCount, boolean isPicture, Long totalCount) {
        this.postId = postId;
        this.nickName = nickName;
        this.title = title;
        this.createdDate = createdDate;
        this.views = views;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.isPicture = isPicture;
        this.totalCount = totalCount;
    }
}
