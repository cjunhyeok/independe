package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.video.FindVideosResponse;
import community.independe.domain.video.Video;
import community.independe.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VideoApiController {

    private final VideoService videoService;

    @GetMapping("/api/videos")
    @Operation(summary = "모든 영상 조회")
    public Result findVideos() {

        List<Video> findVideos = videoService.findAll();

        List<FindVideosResponse> collect = findVideos.stream()
                .map(v -> new FindVideosResponse(
                        v.getVideoUrl(),
                        v.getVideoMasterUrl(),
                        v.getMaterName(),
                        v.getVideoTitle(),
                        v.getIndependentPostType(),
                        v.getViews()
                )).collect(Collectors.toList());

        return new Result(collect);
    }
}
