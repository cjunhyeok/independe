package community.independe.service;

import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import community.independe.repository.PostRepository;
import community.independe.repository.file.FilesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileServiceImpl implements FileService{

    private final String fileDir;
    private final FilesRepository filesRepository;
    private final PostRepository postRepository;

    public FileServiceImpl(@Value("${file.dir}") String fileDir, FilesRepository filesRepository, PostRepository postRepository) {
        this.fileDir = fileDir;
        this.filesRepository = filesRepository;
        this.postRepository = postRepository;
    }

    public String getFullPath(String filename, Long postId) {
        return fileDir + "/" + postId + "/" + filename;
    }

    @Override
    public List<Files> saveFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not exist"));

        List<Files> files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                Files saveFilesBeforeRepository = saveFile(multipartFile, findPost);
                Files saveFiles = filesRepository.save(saveFilesBeforeRepository);
                files.add(saveFiles);
            }
        }

        return files;
    }

    private Files saveFile(MultipartFile multipartFile, Post findPost) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);
        String fullPath = getFullPath(storeFilename, findPost.getId());
        multipartFile.transferTo(new File(fullPath));
        return Files.builder()
                .uploadFilename(originalFilename)
                .storeFilename(storeFilename)
                .filePath(fullPath)
                .post(findPost)
                .build();
    }

    private String createStoreFilename(String originalFilename) {
        // 서버에 저장하는 관리명
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1); // 확장자 .png가져오기
    }
}
