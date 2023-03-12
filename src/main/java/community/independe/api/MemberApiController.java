package community.independe.api;

import community.independe.api.dtos.member.CreateMemberRequest;
import community.independe.domain.member.Address;
import community.independe.domain.member.Member;
import community.independe.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // json
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/api/members/new")
    public ResponseEntity<Long> createMember(@RequestBody @Valid CreateMemberRequest request) {

        log.info("request : {}", request.getNickname());

        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("ROLE_USER")
                .email(request.getEmail())
                .number(request.getNumber())
                .address(new Address(request.getCity(), request.getStreet(), request.getZipcode()))
                .build();

        Long joinMember = memberService.join(member);
        return ResponseEntity.ok(joinMember);
    }
}
