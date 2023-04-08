package community.independe.service;

import community.independe.domain.file.Files;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    List<Files> saveFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException;
}
