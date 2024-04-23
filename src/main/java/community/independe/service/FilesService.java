package community.independe.service;

import community.independe.api.dtos.files.PostFileResponse;
import community.independe.domain.file.Files;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilesService {

    List<Files> saveFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException;

    PostFileResponse findAllFilesByPostId(Long postId);

    Files findById(Long filesId);

    void deleteFile(Long postId);
}
