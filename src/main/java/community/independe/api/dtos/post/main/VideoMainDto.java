package community.independe.api.dtos.post.main;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoMainDto {

    private String title; // 제목
    private String videoUrl; // 영상 주소

    public VideoMainDto(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
    }
}
