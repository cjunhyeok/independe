package community.independe.repository;

import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.repository.alarm.AlarmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

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
        assertThat(savedAlarm.getMember()).isEqualTo(savedMember);
    }
}
