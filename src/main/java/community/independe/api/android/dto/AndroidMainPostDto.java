package community.independe.api.android.dto;

import community.independe.api.dtos.post.main.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AndroidMainPostDto {

    private String todayMent;
    private List<PopularPostDto> popularPostDtos;
    private List<RegionAllPostDto> regionAllPostDtos;
    private List<RegionNotAllPostDto> regionNotAllPostDtos;
    private List<PopularIndependentPostsDto> popularIndependentPostsDtos;
    private List<VideoMainDto> videoMainDtos;
}
