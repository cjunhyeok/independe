package community.independe.api;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTParser;
import community.independe.api.dtos.member.*;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.RefreshTokenException;
import community.independe.security.service.MemberContext;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.MemberService;
import community.independe.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController // json
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final SecuritySigner securitySigner;
    private final JWK jwk;

    @Operation(summary = "회원 등록 요청")
    @PostMapping("/api/members/new")
    public ResponseEntity<Long> createMember(@RequestBody @Valid CreateMemberRequest request) {

        Long joinMember = memberService.join(request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getEmail(),
                request.getNumber());

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

    @PutMapping("/api/oauth/members")
    public ResponseEntity modifyOAuthMembers(@RequestBody OAuthMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        memberService.modifyOAuthMember(loginMember.getId(), request.getNickname(), request.getEmail(), request.getNumber());

        return ResponseEntity.ok("OK");
    }

    @PutMapping("/api/members")
    public ResponseEntity modifyMembers(@RequestBody ModifyMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();

        memberService.modifyMember(
                loginMember.getId(),
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getEmail(),
                request.getNumber());

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/api/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) throws JOSEException {

        String refreshToken = request.getHeader("RefreshToken");

        if (refreshToken == null) {
            throw new RefreshTokenException("RefreshToken Not Exist");
        }

        String newRefreshToken = refreshTokenService.reProvideRefreshToken(request.getRemoteAddr(), refreshToken);
        String username = getUsernameFromToken(newRefreshToken);
        String jwtToken = securitySigner.getJwtToken(username, jwk);
        makeRefreshTokenToCookieAndJwtInHeader(response, newRefreshToken, jwtToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    private void makeRefreshTokenToCookieAndJwtInHeader(HttpServletResponse response, String refreshToken, String jwtToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);

        response.addCookie(refreshTokenCookie);
        response.addHeader("Authorization", "Bearer " + jwtToken);
    }

    private String getUsernameFromToken(String refreshToken) {
        String sampleToken
                = refreshToken.replace("; Secure; HttpOnly", "").replace("Bearer ", "");

        String username;
        try {
            username = (String) JWTParser.parse(sampleToken)
                    .getJWTClaimsSet().getClaim("username");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return username;
    }
}
