package community.independe.api.dtos.post.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainPostDto {

    private String todayMent;
    private List<PopularPostDto> popularPostDtos;
    private List<RegionAllPostDto> regionAllPostDtos;
    private List<RegionNotAllPostDto> regionNotAllPostDtos;
    private List<PopularIndependentPostsDto> popularIndependentPostsDtos;
}
