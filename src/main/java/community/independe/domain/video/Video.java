package community.independe.domain.video;

import community.independe.domain.post.enums.IndependentPostType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {

    @Id @GeneratedValue
    private Long id;

    private String videoUrl; // 실제 영상 url
    private String videoMasterUrl; // 채널 주인 url
    private String materName; // 채널명
    private String videoTitle; // 영상 제목
    private IndependentPostType independentPostType; // 카테고리
    private int views;

    @Builder
    public Video(String videoUrl, String videoMasterUrl, String materName, String videoTitle, IndependentPostType independentPostType, int views) {
        this.videoUrl = videoUrl;
        this.videoMasterUrl = videoMasterUrl;
        this.materName = materName;
        this.videoTitle = videoTitle;
        this.independentPostType = independentPostType;
        this.views = views;
    }
}
