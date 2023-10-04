package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.exception.CustomException;
import community.independe.exception.ErrorCode;
import community.independe.repository.MemberRepository;
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


    @Override
    @Transactional
    public Long join(String username, String password, String nickname, String email, String number) {

        if (checkUsername(username) == false) {
            throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
        }

        if (checkNickname(nickname) == false) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATED);
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role("ROLE_USER")
                .email(email)
                .number(number)
                .build();
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    private boolean checkUsername(String username) {
        Member findUsername = memberRepository.findByUsername(username);

        if (findUsername != null) {
            return false;
        }
        return true;
    }

    private boolean checkNickname(String nickname) {
        Member findNickname = memberRepository.findByNickname(nickname);

        if (findNickname != null) {
            return false;
        }
        return true;
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
