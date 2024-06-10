package community.independe.service;

import community.independe.api.dtos.alarm.AlarmsResponse;
import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;
import community.independe.domain.member.Member;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.repository.alarm.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmServiceImpl implements AlarmService{

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long saveAlarm(String message, Boolean isRead, AlarmType alarmType, Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        Alarm alarm = Alarm.builder()
                .message(message)
                .isRead(isRead)
                .alarmType(alarmType)
                .member(findMember)
                .build();

        Alarm savedAlarm = alarmRepository.save(alarm);

        return savedAlarm.getId();
    }

    @Override
    public List<AlarmsResponse> findAllByMemberId(Long memberId) {
        List<Alarm> findAlarms = alarmRepository.findAllByMemberId(memberId);

        return findAlarms.stream()
                .map(a -> AlarmsResponse.builder()
                        .alarmType(a.getAlarmType())
                        .message(a.getMessage())
                        .isRead(a.getIsRead())
                        .memberId(a.getMember().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
