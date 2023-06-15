package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public Long join(String username, String password, String nickname, String email, String number, String city, String street, String zipcode) {

        if (checkUsername(username) == false) {
            throw new IllegalArgumentException("중복된 Id");
        }

        if (checkNickname(nickname) == false) {
            throw new IllegalArgumentException("중복된 nickname");
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

    @Override
    @Transactional
    public void modifyOAuthMember(Long memberId, String nickname, String email, String number) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("Member not exist")
        );

        findMember.oauthMember(nickname, email, number);
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Member not exist"));
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
    @Transactional
    public void authenticateRegion(Long memberId, RegionType regionType) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("member not exist")
        );

        findMember.authenticateRegion(regionType);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
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
}
