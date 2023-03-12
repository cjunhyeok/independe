package community.independe.api.dtos.post;

import community.independe.domain.comment.Comment;
import community.independe.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostResponse {

    private String title;
    private String content;
    private String nickname;
    private LocalDateTime lastModifiedDate;
    private Integer views;
    private Integer recommendCount;
    private List<PostCommentResponse> comments;

    public PostResponse(Post post, List<Comment> comments) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getMember().getNickname();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.views = post.getViews();
        this.recommendCount = post.getRecommendCount();
        this.comments = comments.stream()
                .map(c -> new PostCommentResponse(c))
                .collect(Collectors.toList());
    }

}
