package community.independe.service;

import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import community.independe.repository.alarm.AlarmRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

    @InjectMocks
    private AlarmServiceImpl alarmService;
    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void saveAlarmTest() {
        // given
        String message = "mockMessage";
        Boolean isRead = false;
        AlarmType alarmType = AlarmType.TALK;
        Long memberId = 1L;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().build()));
        when(alarmRepository.save(any(Alarm.class))).thenReturn(Alarm.builder().build());

        // when
        Long savedAlarmId = alarmService.saveAlarm(message, isRead, alarmType, memberId);

        // then
        verify(memberRepository).findById(memberId);
        verify(alarmRepository).save(any(Alarm.class));
    }

    @Test
    void findAllByMemberIdTest() {
        // given
        Long memberId = 1L;

        // stub
        when(alarmRepository.findAllByMemberId(memberId)).thenReturn(List.of());

        // when
        List<Alarm> findAlarms = alarmService.findAllByMemberId(memberId);

        // then
        verify(alarmRepository).findAllByMemberId(memberId);
        Assertions.assertThat(findAlarms).isNotNull();
    }
}
