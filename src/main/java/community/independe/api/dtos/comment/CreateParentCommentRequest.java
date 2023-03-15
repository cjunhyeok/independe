package community.independe.api.dtos.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateParentCommentRequest {

    private Long memberId;
    private Long postId;
    private String content;
}
