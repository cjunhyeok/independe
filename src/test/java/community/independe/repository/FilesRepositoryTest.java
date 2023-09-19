package community.independe.repository;

import community.independe.domain.file.Files;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.repository.file.FilesRepository;
import community.independe.repository.post.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class FilesRepositoryTest {

    @Autowired
    private FilesRepository filesRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void saveTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Files files = Files.builder()
                .uploadFilename("upload")
                .storeFilename("store")
                .filePath("path")
                .post(savedPost)
                .build();

        // when
        Files savedFiles = filesRepository.save(files);

        // then
        assertThat(savedFiles.getFilePath()).isEqualTo(files.getFilePath());
        assertThat(savedFiles.getUploadFilename()).isEqualTo(files.getUploadFilename());
        assertThat(savedFiles.getStoreFilename()).isEqualTo(files.getStoreFilename());
        assertThat(savedFiles.getPost()).isEqualTo(files.getPost());
    }

    @Test
    public void findAllFilesByPostIdTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        for (int i = 0; i < 5; i++) {

            Files files = Files.builder()
                    .uploadFilename("upload" + i)
                    .storeFilename("store" + i)
                    .filePath("path" + i)
                    .post(savedPost)
                    .build();
            Files savedFiles = filesRepository.save(files);
        }
        Post nextPost = Post.builder()
                .title("nextTitle")
                .content("nextContent")
                .independentPostType(IndependentPostType.ETC)
                .member(member)
                .build();
        Post savedNextPost = postRepository.save(nextPost);
        Files nextFiles = Files.builder()
                .uploadFilename("nextUpload")
                .storeFilename("nextStore")
                .filePath("nextPath")
                .post(savedNextPost)
                .build();
        Files savedNextFiles = filesRepository.save(nextFiles);

        // when
        List<Files> findAllFilesByPostId = filesRepository.findAllFilesByPostId(savedPost.getId());

        // then
        assertThat(findAllFilesByPostId.size()).isEqualTo(5);
        assertThat(findAllFilesByPostId.get(2).getFilePath()).isEqualTo("path2");
    }

    @Test
    void deleteFilesByPostIdTest() {
        // given
        Member member = Member.builder()
                .username("id")
                .password("pass")
                .nickname("nick")
                .build();
        Member savedMember = memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.COOK)
                .member(savedMember)
                .build();
        Post savedPost = postRepository.save(post);

        Files files = Files.builder()
                .uploadFilename("upload")
                .storeFilename("store")
                .filePath("path")
                .post(savedPost)
                .build();
        Files savedFiles = filesRepository.save(files);

        em.flush();
        em.clear();

        // when
        int deleteCount = filesRepository.deleteFilesByPostId(savedPost.getId());

        // then
        assertThat(deleteCount).isEqualTo(1);
        assertThatThrownBy(() -> filesRepository.findById(savedFiles.getId()).orElseThrow(
                () -> new NoSuchElementException("No Element")
        ))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No Element");
    }
}
