package community.independe.service;

import community.independe.domain.alarm.AlarmType;

public interface AlarmService {

    Long saveAlarm(String message, Boolean isRead, AlarmType alarmType, Long memberId);
}
