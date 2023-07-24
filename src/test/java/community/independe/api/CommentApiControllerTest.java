package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.service.CommentService;
import community.independe.service.MemberService;
import community.independe.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        memberService.join("username", "testPass1!", "testNick", null, null);
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }
}
