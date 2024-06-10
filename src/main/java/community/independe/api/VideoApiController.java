package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.video.FindVideosResponse;
import community.independe.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VideoApiController {

    private final VideoService videoService;

    @GetMapping("/api/videos")
    @Operation(summary = "모든 영상 조회")
    public Result findVideos() {

        List<FindVideosResponse> findVideos = videoService.findAll();

        return new Result(findVideos);
    }
}
