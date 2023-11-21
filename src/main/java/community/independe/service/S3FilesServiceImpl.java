package community.independe.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import community.independe.api.dtos.files.PostFileResponse;
import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.file.FilesRepository;
import community.independe.repository.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class S3FilesServiceImpl implements FilesService{

    private final FilesRepository filesRepository;
    private final PostRepository postRepository;
    private final AmazonS3Client amazonS3Client;
    private String bucket;

    public S3FilesServiceImpl(FilesRepository filesRepository, PostRepository postRepository, AmazonS3Client amazonS3Client, @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.filesRepository = filesRepository;
        this.postRepository = postRepository;
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
    }

    @Override
    public List<Files> saveFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException {

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<Files> files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                String originalFilename = multipartFile.getOriginalFilename();
                String storeFilename = createStoreFilename(originalFilename);

                InputStream inputStream = multipartFile.getInputStream();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(multipartFile.getSize());
                metadata.setContentType(multipartFile.getContentType());
                PutObjectRequest request = new PutObjectRequest(bucket, storeFilename, inputStream, metadata);
                amazonS3Client.putObject(request);

                String s3Url = getS3Url(storeFilename);
                Files file = Files.builder()
                        .storeFilename(storeFilename)
                        .uploadFilename(originalFilename)
                        .s3Url(s3Url)
                        .post(findPost)
                        .build();
                filesRepository.save(file);
                files.add(file);
            }
        }

        return files;
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

    public String getS3Url(String filename) {
        return amazonS3Client.getUrl(bucket, filename).toString();
    }

    @Override
    public PostFileResponse findAllFilesByPostId(Long postId) {
        List<Files> findFiles = filesRepository.findAllFilesByPostId(postId);
        PostFileResponse postFileResponse = new PostFileResponse();
        List<String> s3Urls = new ArrayList<>();
        for (Files findFile : findFiles) {
            s3Urls.add(findFile.getS3Url());
        }
        postFileResponse.setS3Urls(s3Urls);
        return postFileResponse;
    }

    @Override
    public Files findById(Long filesId) {
        return filesRepository.findById(filesId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
    }
}
