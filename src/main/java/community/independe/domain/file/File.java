package community.independe.domain.file;

import community.independe.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String uploadFilename; // 사용자가 업로드한 파일명
    private String storeFilename; // 서버에 저장한 파일명

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post; // 파일, 게시글 -> N : 1

    @Builder
    public File(String uploadFilename, String storeFilename, Post post) {
        this.uploadFilename = uploadFilename;
        this.storeFilename = storeFilename;
        this.post = post;
    }
}
