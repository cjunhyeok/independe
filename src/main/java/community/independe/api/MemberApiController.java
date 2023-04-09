package community.independe.api;

import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // json
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "회원 등록 요청")
    @PostMapping("/api/members/new")
    public ResponseEntity<Long> createMember(@RequestBody @Valid CreateMemberRequest request) {

        Long joinMember = memberService.join(request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getEmail(),
                request.getNumber(),
                request.getCity(),
                request.getStreet(),
                request.getZipcode());

        return ResponseEntity.ok(joinMember);
    }
}
