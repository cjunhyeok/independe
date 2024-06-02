package community.independe.api;

import community.independe.api.dtos.files.PostFileResponse;
import community.independe.service.FilesService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilesApiController {

    private final FilesService filesService;
    @GetMapping("/api/files/{postId}")
    @Operation(summary = "게시글 연관 이미지 조회")
    public PostFileResponse postFiles(@PathVariable(name = "postId") Long postId) {

        PostFileResponse findAllFiles = filesService.findAllFilesByPostId(postId);

        return findAllFiles;
    }
}
