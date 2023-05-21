package community.independe.service;

import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import community.independe.repository.file.FilesRepository;
import community.independe.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilesServiceTest {

    @InjectMocks
    private FilesServiceImpl filesService;
    @Mock
    private FilesRepository filesRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    public void saveFilesTest() throws IOException {
        // given
        Long postId = 1L;
        List<MultipartFile> multipartFiles = new ArrayList<>();

        // stub
        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));

        // when
        List<Files> savedFiles = filesService.saveFiles(multipartFiles, postId);

        // then
        assertThat(savedFiles.size()).isEqualTo(multipartFiles.size());
        verify(filesRepository, times(multipartFiles.size())).save(any(Files.class));
    }

    @Test
    public void findByIdTest() {
        // given
        Long id = 1L;
        Files mockFiles = Files.builder().build();

        // stub
        when(filesRepository.findById(id)).thenReturn(Optional.of(mockFiles));

        // when
        Files findFiles = filesService.findById(id);

        // then
        verify(filesRepository, times(1)).findById(id);
        assertThat(findFiles).isEqualTo(mockFiles);
    }

    @Test
    public void findByIdFailTest() {
        // given
        Long id = 1L;

        // stub
        when(filesRepository.findById(id)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> filesService.findById(id));

        // then
        verify(filesRepository, times(1)).findById(id);
        assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}
