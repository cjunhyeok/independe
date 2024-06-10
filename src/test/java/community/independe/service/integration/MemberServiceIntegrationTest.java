package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.service.MemberService;
import community.independe.service.dtos.*;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class MemberServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("요청정보로 회원가입을 한다.")
    void joinTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto joinDto = createJoinDto(username, password, nickname);

        // when
        Long savedMemberId = memberService.join(joinDto);

        // then
        assertThat(savedMemberId).isNotNull();
        Member findMember = memberRepository.findById(savedMemberId).get();
        assertThat(findMember.getUsername()).isEqualTo(username);
        assertThat(findMember.getPassword()).isNotEqualTo(password);
        assertThat(findMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("회원가입 시 아이디가 중복되면 예외가 발생한다.")
    void joinUsernameDuplicatedTest() {
        // given
        String duplicateUsername = "duplicatedUsername";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(duplicateUsername, password, nickname);
        memberService.join(saveMemberDto);

        String newPassword = "password";
        String newNickname = "newNickname";
        JoinServiceDto joinDto = createJoinDto(duplicateUsername, newPassword, newNickname);

        // when // then
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.join(joinDto))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USERNAME_DUPLICATED);
        });
    }

    @Test
    @DisplayName("회원가입 시 닉네임이 중복되면 예외가 발생한다.")
    void joinNicknameDuplicatedTest() {
        // given
        String username = "username";
        String password = "password";
        String duplicateNickname = "duplicateNickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, duplicateNickname);
        memberService.join(saveMemberDto);

        String newUsername = "newUsername";
        String newPassword = "password";
        JoinServiceDto joinDto = createJoinDto(newUsername, newPassword, duplicateNickname);

        // when // then
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.join(joinDto))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_DUPLICATED);
        });
    }

    @Test
    @DisplayName("ID, PASSWORD 를 이용해 로그인한다.")
    void loginTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        String ip = "127.0.0.1";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        LoginServiceDto loginDto = LoginServiceDto
                .builder()
                .username(username)
                .password(password)
                .ip(ip)
                .build();

        // when
        LoginResponse loginResponse = memberService.login(loginDto);

        // then
        assertThat(loginResponse.getAccessToken()).isNotNull();
        assertThat(loginResponse.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인 시 ID 가 일치하지 않으면 예외가 발생한다.")
    void loginUsernameNotMatchTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        String ip = "127.0.0.1";
        String loginUsername = "usernameException";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        LoginServiceDto loginDto = LoginServiceDto
                .builder()
                .username(loginUsername)
                .password(password)
                .ip(ip)
                .build();

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.login(loginDto))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_USERNAME);
        });
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 일치하지 않으면 예외가 발생한다.")
    void loginPasswordNotMatchTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        String ip = "127.0.0.1";
        String loginPassword = "passwordException";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        LoginServiceDto loginDto = LoginServiceDto
                .builder()
                .username(username)
                .password(loginPassword)
                .ip(ip)
                .build();

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.login(loginDto))
                        .isInstanceOf(CustomException.class)
                        .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
        });
    }

    @Test
    @DisplayName("소셜 로그인한 사용자의 정보를 변경한다.")
    void modifyOAuthMemberTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);

        String oauthNickname = "oauthNickname";
        String email = "oauthEmail";
        String number = "oauthNumber";
        ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto
                = ModifyOAuthMemberServiceDto
                .builder()
                .memberId(savedMemberId)
                .nickname(oauthNickname)
                .email(email)
                .number(number)
                .build();

        // when
        memberService.modifyOAuthMember(modifyOAuthMemberServiceDto);

        // then
        Member findMember = memberRepository.findById(savedMemberId).get();
        assertThat(findMember.getId()).isEqualTo(savedMemberId);
        assertThat(findMember.getNickname()).isEqualTo(oauthNickname);
        assertThat(findMember.getEmail()).isEqualTo(email);
        assertThat(findMember.getNumber()).isEqualTo(number);
    }

    @Test
    @DisplayName("소셜 로그인한 사용자 PK 가 잘못된 경우 예외가 발생한다.")
    void oauthModifyMemberFailTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);

        String oauthNickname = "oauthNickname";
        String email = "oauthEmail";
        String number = "oauthNumber";
        ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto
                = ModifyOAuthMemberServiceDto
                .builder()
                .memberId(savedMemberId + 1L)
                .nickname(oauthNickname)
                .email(email)
                .number(number)
                .build();

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.modifyOAuthMember(modifyOAuthMemberServiceDto))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("사용자 정보를 수정한다.")
    void modifyMemberTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);


        String modifyNickname = "modifyNickname";
        String email = "email";
        String number = "number";
        ModifyMemberServiceDto modifyMemberServiceDto =
                ModifyMemberServiceDto
                .builder()
                .memberId(savedMemberId)
                .nickname(modifyNickname)
                .email(email)
                .number(number)
                .build();

        // when
        memberService.modifyMember(modifyMemberServiceDto);

        // then
        Member findMember = memberRepository.findById(savedMemberId).get();
        assertThat(findMember.getId()).isEqualTo(savedMemberId);
        assertThat(findMember.getNickname()).isEqualTo(modifyNickname);
        assertThat(findMember.getEmail()).isEqualTo(email);
        assertThat(findMember.getNumber()).isEqualTo(number);
    }

    @Test
    @DisplayName("사용자 정보 수정 시 PK 가 잘못되면 예외가 발생한다.")
    void modifyMemberFailTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);


        String modifyNickname = "modifyNickname";
        String email = "email";
        String number = "number";
        ModifyMemberServiceDto modifyMemberServiceDto =
                ModifyMemberServiceDto
                        .builder()
                        .memberId(savedMemberId + 1L)
                        .nickname(modifyNickname)
                        .email(email)
                        .number(number)
                        .build();

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.modifyMember(modifyMemberServiceDto))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("사용자 비밀번호를 변경한다.")
    void modifyPasswordTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);
        String modifyPassword = "modifyPassword";

        // when
        memberService.modifyPassword(savedMemberId, modifyPassword);

        // then
        Member findMember = memberRepository.findById(savedMemberId).get();
        boolean matches = passwordEncoder.matches(modifyPassword, findMember.getPassword());
        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("사용자 비밀버호 수정 시 PK 가 잘못되면 예외가 발생한다.")
    void modifyPasswordFailTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);
        String modifyPassword = "modifyPassword";

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.modifyPassword(savedMemberId + 1L, modifyPassword))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("사용자 위치 정보를 변경한다.")
    void authenticateRegionTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);

        RegionType pusan = RegionType.PUSAN;

        // when
        memberService.authenticateRegion(savedMemberId, pusan);

        // then
        Member findMember = memberRepository.findById(savedMemberId).get();
        assertThat(findMember.getRegion()).isEqualTo(pusan);
    }

    @Test
    @DisplayName("사용자 위치 변경 시 PK 가 잘못되면 예외가 발생한다.")
    void authenticateRegionFailTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);
        RegionType pusan = RegionType.PUSAN;

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.authenticateRegion(savedMemberId + 1L, RegionType.PUSAN))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("가입된 아이디 입력 시 false 를 반환한다.")
    void checkDuplicateUsernameFalseTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        // when
        boolean duplicate = memberService.checkDuplicateUsername(username);

        // then
        assertThat(duplicate).isFalse();
    }

    @Test
    @DisplayName("가입되지 않은 아이디 입력 시 true 를 반환한다.")
    void checkDuplicateUsernameTrueTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        String notSavedUsername = "notSaveUsername";

        // when
        boolean duplicate = memberService.checkDuplicateUsername(notSavedUsername);

        // then
        assertThat(duplicate).isTrue();
    }

    @Test
    @DisplayName("가입된 닉네임 입력 시 false 를 반환한다.")
    void checkDuplicateNicknameFalseTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        // when
        boolean duplicate = memberService.checkDuplicateNickname(nickname);

        // then
        assertThat(duplicate).isFalse();
    }

    @Test
    @DisplayName("가입되지 않은 닉네임 입력 시 true 를 반환한다.")
    void checkDuplicateNicknameTrueTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        memberService.join(saveMemberDto);

        String notSaveNickname = "notSaveNickname";

        // when
        boolean duplicate = memberService.checkDuplicateNickname(notSaveNickname);

        // then
        assertThat(duplicate).isTrue();
    }

    @Test
    @DisplayName("PK 로 사용자 정보를 조회한다.")
    void findMemberByIdTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);

        // when
        FindMemberDto findMember = memberService.findMemberById(savedMemberId);

        // then
        assertThat(findMember.getId()).isEqualTo(savedMemberId);
        assertThat(findMember.getUsername()).isEqualTo(username);
        assertThat(findMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("PK 를 잘못 입력하면 사용자 정보 조회 시 예외가 발생한다.")
    void findMemberByIdFailTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";
        JoinServiceDto saveMemberDto = createJoinDto(username, password, nickname);
        Long savedMemberId = memberService.join(saveMemberDto);

        // when
        AbstractObjectAssert<?, CustomException> extracting =
                assertThatThrownBy(() -> memberService.findMemberById(savedMemberId + 1L))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    private JoinServiceDto createJoinDto(String username, String password, String nickname) {
        return JoinServiceDto
                .builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email("email")
                .number("number")
                .isPrivacyCheck(true)
                .isTermOfUseCheck(true)
                .build();
    }
}
