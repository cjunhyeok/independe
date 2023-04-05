package community.independe.api.dtos.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostsResponse {

    private Long postId;
    private String nickName;
    private String title;
    private LocalDateTime lastModifiedDate;
    private Integer views; // 조회수
    private Integer recommendCount; // 추천수
    private Long commentCount; // 댓글수
}
