package community.independe.api.dtos.files;

import community.independe.domain.file.Files;
import community.independe.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@NoArgsConstructor
public class PostFileResponse {

    private List<byte[]> files;

    public PostFileResponse(Post post, List<Files> files) {
        this.files = files.stream()
                .map(f -> {
                    try {
                        return getImageBytes(f, post.getId());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public byte[] getImageBytes(Files files, Long postId) throws IOException {
        UrlResource resource = new UrlResource("file:" + files.getFilePath());

        InputStream inputStream = resource.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();

        return bytes;
    }
}
