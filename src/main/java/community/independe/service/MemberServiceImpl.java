package community.independe.service;

import com.nimbusds.jose.JOSEException;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecuritySigner securitySigner;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public Long join(JoinServiceDto joinServiceDto) {

        String username = joinServiceDto.getUsername();
        Member findUsername = memberRepository.findByUsername(username);
        if (findUsername != null) {
            throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
        }

        String nickname = joinServiceDto.getNickname();
        Member findNickname = memberRepository.findByNickname(nickname);
        if (findNickname != null) {
            throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(joinServiceDto.getPassword()))
                .nickname(nickname)
                .role("ROLE_USER")
                .email(joinServiceDto.getEmail())
                .number(joinServiceDto.getNumber())
                .isPrivacyCheck(joinServiceDto.getIsPrivacyCheck())
                .isTermOfUseCheck(joinServiceDto.getIsTermOfUseCheck())
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Override
    public LoginResponse login(LoginServiceDto loginServiceDto) {

        Member findMember = memberRepository.findByUsername(loginServiceDto.getUsername());
        if (findMember == null) {
            throw new CustomException(ErrorCode.INVALID_USERNAME);
        }

        if (!passwordEncoder.matches(loginServiceDto.getPassword(), findMember.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        try {
            String findUsername = findMember.getUsername();
            String role = findMember.getRole();

            // 해당 부분 리펙토링이 필요할듯
            String jwtToken = securitySigner.getJwtToken(findUsername);
            String refreshToken = securitySigner.getRefreshJwtToken(findUsername);
            refreshTokenService.save(loginServiceDto.getIp(), role, refreshToken, findUsername);

            return LoginResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (JOSEException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void modifyOAuthMember(ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto) {
        Member findMember = memberRepository.findById(modifyOAuthMemberServiceDto.getMemberId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        findMember.oauthMember(
                modifyOAuthMemberServiceDto.getNickname(),
                modifyOAuthMemberServiceDto.getEmail(),
                modifyOAuthMemberServiceDto.getNumber());
    }

    @Override
    @Transactional
    public void modifyMember(ModifyMemberServiceDto modifyMemberServiceDto) {
        Member findMember = memberRepository.findById(modifyMemberServiceDto.getMemberId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        findMember.modifyMember(
                modifyMemberServiceDto.getNickname(),
                modifyMemberServiceDto.getEmail(),
                modifyMemberServiceDto.getNumber());
    }

    @Override
    @Transactional
    public void modifyPassword(Long memberId, String password) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        findMember.modifyPassword(passwordEncoder.encode(password));
    }

    @Override
    @Transactional
    public void authenticateRegion(Long memberId, RegionType regionType) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        findMember.authenticateRegion(regionType);
    }

    @Override
    public boolean checkDuplicateUsername(String username) {
        Member findMember = memberRepository.findByUsername(username);

        if (findMember == null) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean checkDuplicateNickname(String nickname) {
        Member findMember = memberRepository.findByNickname(nickname);

        if (findMember == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public FindMemberDto findMemberById(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        return FindMemberDto.builder()
                .username(findMember.getUsername())
                .nickname(findMember.getNickname())
                .email(findMember.getEmail())
                .number(findMember.getNumber())
                .build();
    }
}
