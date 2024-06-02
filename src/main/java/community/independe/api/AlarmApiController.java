package community.independe.api;

import community.independe.api.dtos.Result;
import community.independe.api.dtos.alarm.AlarmsResponse;
import community.independe.repository.MemberRepository;
import community.independe.security.service.MemberContext;
import community.independe.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AlarmApiController {

    private final AlarmService alarmService;
    private final MemberRepository memberRepository;

    @GetMapping("/api/alarms")
    @Operation(summary = "내 알람 조회 *")
    public Result alarmList(@AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext == null ? null : memberContext.getMemberId();

        List<AlarmsResponse> findAlarmResponses = alarmService.findAllByMemberId(loginMemberId);

        return new Result(findAlarmResponses);
    }
}
