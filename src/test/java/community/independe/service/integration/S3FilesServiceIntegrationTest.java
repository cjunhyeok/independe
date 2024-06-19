package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.files.PostFileResponse;
import community.independe.domain.file.Files;
import community.independe.domain.member.Member;
import community.independe.domain.post.Post;
import community.independe.domain.post.enums.IndependentPostType;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.file.FilesRepository;
import community.independe.repository.post.PostRepository;
import community.independe.service.FilesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class S3FilesServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private FilesService filesService;
    @Autowired
    private FilesRepository filesRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("게시글의 파일을 저장한다.")
    void saveFilesTest() throws IOException {
        // given
        Member savedMember = createMember();
        Post savedPost = creatPost(savedMember);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        multipartFiles.add(new MockMultipartFile("secondFile", "secondFileContent".getBytes()));

        // when
        List<Long> filesId = filesService.saveFiles(multipartFiles, savedPost.getId());

        // then
        assertThat(filesId).hasSize(2);
    }

    @Test
    @DisplayName("파일 저장 시 게시글 PK 를 잘못입력하면 예외가 발생한다.")
    void saveFilesFailTest() {
        // given
        Member savedMember = createMember();
        Post savedPost = creatPost(savedMember);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        multipartFiles.add(new MockMultipartFile("secondFile", "secondFileContent".getBytes()));

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> filesService.saveFiles(multipartFiles, savedPost.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글과 연관된 파일을 조회한다.")
    void findAllFilesByPostIdTest() throws IOException {
        // given
        Member savedMember = createMember();
        Post savedPost = creatPost(savedMember);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        multipartFiles.add(new MockMultipartFile("secondFile", "secondFileContent".getBytes()));
        List<Long> filesId = filesService.saveFiles(multipartFiles, savedPost.getId());

        // when
        PostFileResponse postFileResponse = filesService.findAllFilesByPostId(savedPost.getId());

        // then
        assertThat(postFileResponse.getS3Urls()).hasSize(2);
    }

    @Test
    @DisplayName("게시글과 연관된 파일 조회 시 게시글 PK 를 잘못 입력하면 예외가 발생한다.")
    void findAllFilesByPostIdFailTest() throws IOException {
        // given
        Member savedMember = createMember();
        Post savedPost = creatPost(savedMember);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        multipartFiles.add(new MockMultipartFile("secondFile", "secondFileContent".getBytes()));
        List<Long> filesId = filesService.saveFiles(multipartFiles, savedPost.getId());

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> filesService.findAllFilesByPostId(savedPost.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("게시글과 관련된 파일을 삭제한다.")
    void deleteFileTest() throws IOException {
        // given
        Member savedMember = createMember();
        Post savedPost = creatPost(savedMember);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile("firstFile", "firstFileContent".getBytes()));
        multipartFiles.add(new MockMultipartFile("secondFile", "secondFileContent".getBytes()));
        List<Long> filesId = filesService.saveFiles(multipartFiles, savedPost.getId());

        // when
        filesService.deleteFile(savedPost.getId());

        em.flush();
        em.clear();

        // then
        List<Files> findFiles = filesRepository.findAllFilesByPostId(savedPost.getId());
        assertThat(findFiles).isEmpty();
    }

    private Member createMember() {
        Member member = Member
                .builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .region(RegionType.SEOUL)
                .role("ROLE_USER")
                .email("email")
                .number("number")
                .build();
        return memberRepository.save(member);
    }

    private Post creatPost(Member member) {
        Post post = Post
                .builder()
                .title("title")
                .content("content")
                .independentPostType(IndependentPostType.ETC)
                .member(member)
                .build();

        return postRepository.save(post);
    }
}
