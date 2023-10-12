package community.independe.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.file.FilesRepository;
import community.independe.repository.post.PostRepository;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3FilesServiceTest {

    @InjectMocks
    private S3FilesServiceImpl filesService;
    @Mock
    private FilesRepository filesRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    void saveFilesTest() throws IOException {
        // given
        Long postId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        multipartFiles.add(new MockMultipartFile("secondFile", "secondFileContent".getBytes()));

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(amazonS3Client.getUrl(any(), any())).thenReturn(new URL("https://example-image.jpg"));
        when(amazonS3Client.putObject(any())).thenReturn(any());

        // when
        List<Files> files = filesService.saveFiles(multipartFiles, postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(amazonS3Client, times(2)).putObject(any(PutObjectRequest.class)); // s3 업로드 횟수 확인
        verify(amazonS3Client, times(2)).getUrl(any(), any()); // s3 조회 횟수 확인
    }

    @Test
    void saveFilesPostFailTest() {
        // given
        Long postId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(() -> filesService.saveFiles(multipartFiles, postId))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
        verify(postRepository, times(1)).findById(postId);
        verifyNoInteractions(amazonS3Client);
        verifyNoInteractions(filesRepository);
    }

    @Test
    void findAllFilesByPostIdTest() {
        // given
        Long postId = 1L;
        List<Files> files = new ArrayList<>();

        // stub
        when(filesRepository.findAllFilesByPostId(postId)).thenReturn(files);

        // when
        List<Files> findFiles = filesService.findAllFilesByPostId(postId);

        // then
        assertThat(findFiles).isEqualTo(files);
        verify(filesRepository, times(1)).findAllFilesByPostId(postId);
    }

    @Test
    void findByIdTest() {
        // given
        Long filesId = 1L;
        Files files = Files.builder().build();


        // stub
        when(filesRepository.findById(filesId)).thenReturn(Optional.of(files));

        // when
        Files findFiles = filesService.findById(filesId);

        // then
        assertThat(findFiles).isEqualTo(files);
        verify(filesRepository, times(1)).findById(filesId);
    }
}
