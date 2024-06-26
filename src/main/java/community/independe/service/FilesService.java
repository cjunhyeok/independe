package community.independe.service;

import community.independe.api.dtos.files.PostFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilesService {

    List<Long> saveFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException;

    PostFileResponse findAllFilesByPostId(Long postId);

    void deleteFile(Long postId);
}
