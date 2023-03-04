package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long join(Member member) {
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Member not exist"));
    }
}
