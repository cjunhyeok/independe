package community.independe.api;

import community.independe.api.dtos.alarm.AlarmMessage;
import community.independe.repository.MemberRepository;
import community.independe.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AlarmApiController {

    private final AlarmService alarmService;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate; // 특정 상대에게 메시지를 보내기 위한 객체

    @MessageMapping("/post-alarm")
    public AlarmMessage receiveAlarm(@Payload AlarmMessage message) {
        simpMessagingTemplate.convertAndSendToUser(message.getMemberId().toString(), "/alarm", message.getMessage());

        alarmService.saveAlarm(message.getMessage(), false, message.getAlarmType(), message.getMemberId());
        return message;
    }
}
