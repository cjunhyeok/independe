package community.independe.service;

import community.independe.domain.alarm.Alarm;
import community.independe.domain.alarm.AlarmType;

import java.util.List;

public interface AlarmService {

    Long saveAlarm(String message, Boolean isRead, AlarmType alarmType, Long memberId);
    List<Alarm> findAllByMemberId(Long memberId);
}
