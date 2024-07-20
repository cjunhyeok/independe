package community.independe.api;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWT;
import community.independe.api.dtos.Result;
import community.independe.api.dtos.member.*;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.security.provider.JwtParser;
import community.independe.security.service.MemberContext;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.CommentService;
import community.independe.service.MemberService;
import community.independe.service.PostService;
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

@Slf4j
@RestController // json
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final RefreshTokenService refreshTokenService;
    private final SecuritySigner securitySigner;
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

        boolean isDuplicateNot = memberService.checkDuplicateUsername(request.getUsername());

        return new DuplicateResponse(isDuplicateNot);
    }

    @Operation(summary = "닉네임 중복 확인")
    @PostMapping("/api/members/nickname")
    public DuplicateResponse duplicateNickname(@RequestBody DuplicateNicknameRequest request) {

        boolean isDuplicateNot = memberService.checkDuplicateNickname(request.getNickname());

        return new DuplicateResponse(isDuplicateNot);
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

        Long loginMemberId = memberContext.getMemberId();
        RegionType regionType = regionProvider(request.getRegion());

        memberService.authenticateRegion(loginMemberId, regionType);

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

    @PutMapping("/api/oauth/members")
    @Operation(summary = "소셜 로그인 후 추가 정보 입력 api *")
    public ResponseEntity modifyOAuthMembers(@RequestBody OAuthMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext.getMemberId();
        ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto = OAuthMemberRequest.requestToModifyOAuthMemberServiceDto(request, loginMemberId);

        memberService.modifyOAuthMember(modifyOAuthMemberServiceDto);

        return ResponseEntity.ok("OK");
    }

    @PutMapping("/api/members")
    @Operation(summary = "회원 정보 수정 *")
    public ResponseEntity modifyMembers(@RequestBody ModifyMemberRequest request,
                                        @AuthenticationPrincipal MemberContext memberContext) {

        Long loginMemberId = memberContext.getMemberId();
        ModifyMemberServiceDto modifyMemberServiceDto
                = ModifyMemberRequest.requestToModifyMemberServiceDto(request, loginMemberId);

        memberService.modifyMember(modifyMemberServiceDto);

        return ResponseEntity.ok("OK");
    }

    @PutMapping("/api/members/password")
    @Operation(summary = "회원 비밀번호 수정 *")
    public ResponseEntity modifyMember(@RequestBody ModifyPasswordRequest request,
                                       @AuthenticationPrincipal MemberContext memberContext) {
        Long loginMemberId = memberContext.getMemberId();

        memberService.modifyPassword(loginMemberId, request.getPassword());

        return ResponseEntity.ok("비밀번호 수정 완료");
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
        String jwtToken = securitySigner.getJwtToken(username);
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
        Long loginMemberId = memberContext.getMemberId();

        // 회원 정보 조회
        FindMemberDto findMemberDto = memberService.findMemberById(loginMemberId);

        MyPageResponse myPageResponse = MyPageResponse.builder()
                .username(findMemberDto.getUsername())
                .nickname(findMemberDto.getNickname())
                .email(findMemberDto.getEmail())
                .number(findMemberDto.getNumber())
                .build();

        return new Result(myPageResponse);
    }

    // 작성한 글 목록 (활동내역)
    @GetMapping("/api/member/post")
    @Operation(summary = "작성한 글 목록 조회 * (마이페이지)")
    public Result getMyPost(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                            @AuthenticationPrincipal MemberContext memberContext) {
        Long loginMemberId = memberContext.getMemberId();
        Long totalCount = 0L;

        List<MyPostServiceDto> response = postService.findMyPost(loginMemberId, page, size);
        if (!response.isEmpty()) {
            totalCount = response.get(0).getTotalCount();
        }

        return new Result(response, totalCount);
    }

    // 작성한 댓글 목록 * (활동내역)
    @GetMapping("/api/member/comment")
    @Operation(summary = "작성한 댓글 목록 조회 * (마이페이지)")
    public Result getMyComment(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                               @AuthenticationPrincipal MemberContext memberContext) {
        Long loginMemberId = memberContext.getMemberId();
        Long totalCount = 0L;

        List<MyCommentServiceDto> response = commentService.getMyComment(loginMemberId, page, size);
        if (!response.isEmpty()) {
            totalCount = response.get(0).getTotalCount();
        }

        return new Result(response, totalCount);
    }
}
