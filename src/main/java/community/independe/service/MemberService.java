package community.independe.service;

import community.independe.domain.member.Member;

public interface MemberService {

    Long join(Member member);

    Member findById(Long id);
}
