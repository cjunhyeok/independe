package community.independe.api;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWT;
import community.independe.api.dtos.Result;
import community.independe.api.dtos.member.*;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.security.provider.JwtParser;
import community.independe.security.service.MemberContext;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.MemberService;
import community.independe.service.RefreshTokenService;
import community.independe.service.dtos.*;
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
    private final JwtParser jwtParser;

    @Operation(summary = "회원 등록 요청")
    @PostMapping("/api/members/new")
    public ResponseEntity<Long> createMember(@RequestBody @Valid CreateMemberRequest request) {

        JoinServiceDto joinServiceDto = CreateMemberRequest.requestToServiceDto(request);
        Long joinMember = memberService.join(joinServiceDto);

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

    @Operation(summary = "로그인")
    @PostMapping("/api/member/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest,
                                HttpServletRequest request, HttpServletResponse response) {

        LoginServiceDto loginServiceDto = LoginRequest.loginRequestToLoginServiceDto(loginRequest, request.getRemoteAddr());
        LoginResponse loginResponse = memberService.login(loginServiceDto);

        // header, cookie 에 access, refresh 토큰 넣기
        response.addHeader("Authorization", "Bearer " + loginResponse.getAccessToken());
        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Login Success");
    }

    @Operation(summary = "위치 인증 *")
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
            throw new CustomException(ErrorCode.REGION_NOT_AUTHENTICATE);
        }
    }

    @GetMapping("/api/members")
    @Operation(summary = "모든 회원 정보 반환")
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
    @Operation(summary = "소셜 로그인 후 추가 정보 입력 api *")
    public ResponseEntity modifyOAuthMembers(@RequestBody OAuthMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();
        ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto = OAuthMemberRequest.requestToModifyOAuthMemberServiceDto(request, loginMember.getId());

        memberService.modifyOAuthMember(modifyOAuthMemberServiceDto);

        return ResponseEntity.ok("OK");
    }

    @PutMapping("/api/members")
    @Operation(summary = "회원 정보 수정 *")
    public ResponseEntity modifyMembers(@RequestBody ModifyMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Member loginMember = memberContext.getMember();
        ModifyMemberServiceDto modifyMemberServiceDto
                = ModifyMemberRequest.requestToModifyMemberServiceDto(request, loginMember.getId());

        memberService.modifyMember(modifyMemberServiceDto);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/api/refreshToken")
    @Operation(summary = "리프레시 토큰 재발급")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) throws JOSEException, ParseException {

        String refreshToken = request.getHeader("RefreshToken");

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        String username = getUsernameFromToken(refreshToken);
        String newRefreshToken = refreshTokenService.reProvideRefreshToken(username, request.getRemoteAddr(), refreshToken);
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

    private String getUsernameFromToken(String refreshToken) throws ParseException {
        String sampleToken
                = refreshToken.replace("; Secure; HttpOnly", "").replace("Bearer ", "");

        JWT parsedJwt = jwtParser.parse(sampleToken);

        return jwtParser.getClaim(parsedJwt, "username");
    }

    @GetMapping("/api/member")
    @Operation(summary = "마이페이지 회원 데이터 조회 *")
    public Result<MyPageResponse> getMyPage(@AuthenticationPrincipal MemberContext memberContext) {
        Member loginMember = memberContext.getMember();

        MyPageResponse myPageResponse = MyPageResponse.builder()
                .username(loginMember.getUsername())
                .nickname(loginMember.getNickname())
                .email(loginMember.getEmail())
                .number(loginMember.getNumber())
                .build();

        return new Result(myPageResponse);
    }
}
