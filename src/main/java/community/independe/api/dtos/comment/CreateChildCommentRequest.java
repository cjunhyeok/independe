package community.independe.api.dtos.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateChildCommentRequest {

    private Long memberId;
    private Long postId;
    private Long parentId;
    private String content;
}
