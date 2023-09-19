package community.independe.repository;

import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.repository.alarm.AlarmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class AlarmRepositoryTest {

    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveTest() {
        // given
        Member member = Member.builder()
                .username("username")
                .password("pass12!")
                .build();
        Member savedMember = memberRepository.save(member);

        Alarm alarm = Alarm.builder()
                .message("message")
                .alarmType(AlarmType.TALK)
                .isRead(false)
                .member(savedMember)
                .build();

        // when
        Alarm savedAlarm = alarmRepository.save(alarm);

        // then
        assertThat(savedAlarm.getId()).isEqualTo(savedAlarm.getId());
        assertThat(savedAlarm.getMember()).isEqualTo(savedMember);
        assertThat(savedAlarm.getAlarmType()).isEqualTo(savedAlarm.getAlarmType());
        assertThat(savedAlarm.getMessage()).isEqualTo(savedAlarm.getMessage());
        assertThat(savedAlarm.getIsRead()).isEqualTo(savedAlarm.getIsRead());
        assertThat(savedAlarm.getAlarmType().getDescription()).isEqualTo(savedAlarm.getAlarmType().getDescription());
    }

    @Test
    void findAllByMemberIdTest() {
        // given
        Member member = Member.builder()
                .username("username")
                .password("pass12!")
                .build();
        Member savedMember = memberRepository.save(member);

        Alarm alarm = Alarm.builder()
                .message("message")
                .alarmType(AlarmType.POST)
                .isRead(false)
                .member(savedMember)
                .build();
        Alarm savedAlarm = alarmRepository.save(alarm);

        Alarm alarm2 = Alarm.builder()
                .message("message")
                .alarmType(AlarmType.COMMENT)
                .isRead(false)
                .member(savedMember)
                .build();
        Alarm savedAlarm2 = alarmRepository.save(alarm2);

        // when
        List<Alarm> findAlarms = alarmRepository.findAllByMemberId(savedMember.getId());

        // then
        assertThat(findAlarms.size()).isEqualTo(2);
    }
}
