package community.independe.api.dtos.post;

import community.independe.domain.comment.Comment;
import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Slf4j
public class PostResponse {

    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createdDate;
    private Integer views;
    private Integer recommendCount;
    private Long commentCount;
    private List<PostCommentResponse> comments;
    private List<byte[]> files;

    public PostResponse(Post post, List<Comment> comments, List<Files> files, Long commentCount) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getMember().getNickname();
        this.createdDate = post.getCreatedDate();
        this.views = post.getViews();
        this.recommendCount = post.getRecommendCount();
        this.commentCount = commentCount;
        this.comments = comments.stream()
                .map(PostCommentResponse::new)
                .collect(Collectors.toList());
        this.files = files.stream()
                .map(f -> {
                    try {
                        return getImageBytes(f, post.getId());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public byte[] getImageBytes(Files files, Long postId) throws IOException {
        UrlResource resource = new UrlResource("file:" + files.getFilePath());

        InputStream inputStream = resource.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();

        return bytes;
    }

}
