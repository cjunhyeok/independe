package community.independe.api;

import community.independe.api.dtos.member.*;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.security.service.MemberContext;
import community.independe.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @Operation(summary = "아이디 중복 확인")
    @PostMapping("/api/members/username")
    public DuplicateResponse duplicateUsername(@RequestBody DuplicateUsernameRequest request) {

        Member findMember = memberService.findByUsername(request.getUsername());

        if (findMember == null) {
            return new DuplicateResponse(true);
        } else {
            return new DuplicateResponse(false);
        }
    }

    @Operation(summary = "닉네임 중복 확인")
    @PostMapping("/api/members/nickname")
    public DuplicateResponse duplicateNickname(@RequestBody DuplicateNicknameRequest request) {

        Member findMember = memberService.findByNickname(request.getNickname());

        if (findMember == null) {
            return new DuplicateResponse(true);
        } else {
            return new DuplicateResponse(false);
        }
    }

    @Operation(summary = "위치 인증")
    @PostMapping("/api/members/region")
    public ResponseEntity authenticateRegion(@RequestBody AuthenticationRegionRequest request,
                                 @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();
        RegionType regionType = regionProvider(request.getRegion());

        memberService.authenticateRegion(loginMember.getId(), regionType);

        return ResponseEntity.ok("Success Region Authentication");
    }

    @GetMapping("/api/members")
    public List<MembersDto> members(@AuthenticationPrincipal MemberContext memberContext) {

        List<Member> members = memberService.findAll();

        return members.stream().map(
                        m -> new MembersDto(
                                m.getId(),
                                m.getNickname()
                        ))
                .collect(Collectors.toList());
    }

    @PutMapping("/api/members")
    public ResponseEntity modifyMembers(@RequestBody OAuthMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        memberService.modifyOAuthMember(loginMember.getId(), request.getNickname(), request.getEmail(), request.getNumber());

        return ResponseEntity.ok("OK");
    }

    private RegionType regionProvider(String region) {

        log.info("region : " + region);

        if (region.equals("서울")) {
            return RegionType.SEOUL;
        } else if (region.equals("울산")) {
            return RegionType.ULSAN;
        } else if (region.equals("부산")) {
            return RegionType.PUSAN;
        } else if (region.equals("경남")) {
            return RegionType.KYEONGNAM;
        } else {
            throw new IllegalArgumentException("region not exist");
        }
    }
}
