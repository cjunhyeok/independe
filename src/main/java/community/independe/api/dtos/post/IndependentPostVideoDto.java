package community.independe.api.dtos.post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IndependentPostVideoDto {

    private String title; // 제목
    private String videoUrl; // 영상 주소

    public IndependentPostVideoDto(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
    }
}
