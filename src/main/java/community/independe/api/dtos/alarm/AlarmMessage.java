package community.independe.api.dtos.alarm;

import community.independe.domain.alarm.AlarmType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmMessage {
    private Long memberId;
    private String message;
    private AlarmType alarmType;

    @Builder
    public AlarmMessage(Long memberId, String message, AlarmType alarmType) {
        this.memberId = memberId;
        this.message = message;
        this.alarmType = alarmType;
    }
}
