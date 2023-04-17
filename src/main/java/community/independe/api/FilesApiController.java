package community.independe.api;

import community.independe.api.dtos.files.PostFileResponse;
import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import community.independe.service.FilesService;
import community.independe.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilesApiController {

    private final FilesService filesService;
    private final PostService postService;

    @GetMapping("/api/files/{postId}")
    public PostFileResponse postFiles(@PathVariable(name = "postId") Long postId) {

        Post findPost = postService.findById(postId);
        List<Files> findAllFiles = filesService.findAllFilesByPostId(findPost.getId());
        return new PostFileResponse(findPost, findAllFiles);
    }
}
