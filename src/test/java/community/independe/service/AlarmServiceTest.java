package community.independe.service;

import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.exception.notfound.MemberNotFountException;
import community.independe.repository.MemberRepository;
import community.independe.repository.alarm.AlarmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void saveAlarmFailTest() {
        // given
        String message = "mockMessage";
        Boolean isRead = false;
        AlarmType alarmType = AlarmType.TALK;
        Long memberId = 1L;

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> alarmService.saveAlarm(message, isRead, alarmType, memberId))
                .isInstanceOf(MemberNotFountException.class)
                .hasMessage("Member Not Exist");

        // then
        verify(memberRepository, times(1)).findById(memberId);
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
        assertThat(findAlarms).isNotNull();
    }
}
