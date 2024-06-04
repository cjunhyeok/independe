package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.api.dtos.alarm.AlarmsResponse;
import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.alarm.AlarmRepository;
import community.independe.service.AlarmService;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class AlarmServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("알람 정보를 저장한다.")
    void saveAlarmTest() {
        // given
        String message = "message";
        Boolean isRead = false;
        AlarmType post = AlarmType.POST;
        Member savedMember = createMember();

        // when
        Long savedAlarmId = alarmService.saveAlarm(message, isRead, post, savedMember.getId());

        // then
        Alarm findAlarm = alarmRepository.findById(savedAlarmId).get();
        assertThat(findAlarm.getId()).isEqualTo(savedAlarmId);
        assertThat(findAlarm.getMessage()).isEqualTo(message);
        assertThat(findAlarm.getIsRead()).isEqualTo(isRead);
        assertThat(findAlarm.getAlarmType()).isEqualTo(post);
        assertThat(findAlarm.getMember().getId()).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("회원 PK를 잘못 입력하면 알람 정보 저장 시 예외가 발생한다.")
    void savedAlarmFailTest() {
        // given
        String message = "message";
        Boolean isRead = false;
        AlarmType post = AlarmType.POST;
        Member savedMember = createMember();

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> alarmService.saveAlarm(message, isRead, post, savedMember.getId() + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("")
    void findAllByMemberIdTest() {
        // given
        String message = "message";
        Boolean isRead = false;
        AlarmType post = AlarmType.POST;
        AlarmType talk = AlarmType.TALK;
        Member savedMember = createMember();
        alarmService.saveAlarm(message, isRead, post, savedMember.getId());
        alarmService.saveAlarm(message, isRead, talk, savedMember.getId());

        // when
        List<AlarmsResponse> findAlarms = alarmService.findAllByMemberId(savedMember.getId());

        // then
        assertThat(findAlarms).hasSize(2);
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("id")
                .password("1234")
                .nickname("nick")
                .role("ROLE_USER")
                .build();
        return memberRepository.save(member);
    }
}
