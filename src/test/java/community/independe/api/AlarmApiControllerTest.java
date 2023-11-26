package community.independe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.service.AlarmService;
import community.independe.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlarmApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LoginMemberInjector injector;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;

    @BeforeEach
    public void setup() throws Exception {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        injector.makeAccessAndRefreshToken();
        accessToken = injector.getAccessToken();
    }

    @AfterEach
    void afterTest() {
        transactionManager.rollback(transactionStatus);
    }

    @Test
    void alarmListTest() throws Exception {
        // given
        Member findMember = memberService.findByUsername("testUsername");
        Long savedAlarmId = alarmService.saveAlarm("message", false, AlarmType.COMMENT, findMember.getId());

        // when
        ResultActions perform = mockMvc.perform(get("/api/alarms")
                .header("Authorization", accessToken));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].memberId").value(findMember.getId()))
                .andExpect(jsonPath("$.data[0].message").value("message"))
                .andExpect(jsonPath("$.data[0].alarmType").value(AlarmType.COMMENT.name()))
                .andExpect(jsonPath("$.data[0].isRead").value(false));
    }
}
