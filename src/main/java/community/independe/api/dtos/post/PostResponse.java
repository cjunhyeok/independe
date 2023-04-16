package community.independe.api.dtos.post;

import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionPostType;
import community.independe.domain.post.enums.RegionType;
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
    private String independentPostType;
    private String regionType;
    private String regionPostType;
    private IndependentPostType independentPostTypeEn;
    private RegionType regionTypeEn;
    private RegionPostType regionPostTypeEn;
    private Integer views;
    private Long recommendCount;
    private Long commentCount;
    private Boolean isRecommend;
    private Boolean isFavorite;
    private Boolean isReport;
    private List<PostCommentResponse> comments;
    private List<byte[]> files;

    public PostResponse(Post post, List<PostCommentResponse> comments, List<Files> files, Long commentCount, Long recommendCount, Boolean isRecommend, Boolean isFavorite, Boolean isReport) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getMember().getNickname();
        this.createdDate = post.getCreatedDate();
        this.independentPostType = (post.getIndependentPostType() == null) ? null : post.getIndependentPostType().getDescription();
        this.regionType = (post.getRegionType() == null) ? null : post.getRegionType().getDescription();
        this.regionPostType = (post.getRegionPostType() == null) ? null : post.getRegionPostType().getDescription();
        this.independentPostTypeEn = post.getIndependentPostType();
        this.regionTypeEn = post.getRegionType();
        this.regionPostTypeEn = post.getRegionPostType();
        this.views = post.getViews();
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
        this.isRecommend = isRecommend;
        this.isFavorite = isFavorite;
        this.isReport = isReport;
        this.comments = comments;
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
