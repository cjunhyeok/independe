package community.independe;

import community.independe.repository.MemberRepository;
import community.independe.repository.comment.CommentRepository;
import community.independe.repository.keyword.KeywordRepository;
import community.independe.repository.post.PostRepository;
import community.independe.repository.video.VideoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InitDbTest {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void initDb() {
        // given
        InitDB.InitService initService = new InitDB.InitService(em, memberRepository, postRepository, commentRepository, keywordRepository, videoRepository, passwordEncoder);
        InitDB initDB = new InitDB(initService);
        initDB.init();
    }
}
