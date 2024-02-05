package community.independe.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
import community.independe.security.signature.SecuritySigner;
import community.independe.service.dtos.JoinServiceDto;
import community.independe.service.dtos.LoginResponse;
import community.independe.service.dtos.LoginServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecuritySigner securitySigner;
    private final RefreshTokenService refreshTokenService;
    private final JWK jwk;

    @Override
    @Transactional
    public Long join(JoinServiceDto joinServiceDto) {

        String username = joinServiceDto.getUsername();
        String nickname = joinServiceDto.getNickname();

        Member findUsername = memberRepository.findByUsername(username);
        if (findUsername != null) {
            throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
        }

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

            String jwtToken = securitySigner.getJwtToken(findUsername, jwk);
            String refreshToken = securitySigner.getRefreshJwtToken(findUsername, jwk);
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
    public void modifyOAuthMember(Long memberId, String nickname, String email, String number) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        findMember.oauthMember(nickname, email, number);
    }

    @Override
    @Transactional
    public void modifyMember(Long memberId, String username, String password, String nickname, String email, String number) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        findMember.modifyMember(username, password, nickname, email, number);
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
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    @Override
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Override
    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
