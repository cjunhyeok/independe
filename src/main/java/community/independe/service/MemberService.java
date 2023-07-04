package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;

import java.util.List;

public interface MemberService {

    Long join(String username, String password, String nickname, String email, String number);

    void modifyOAuthMember(Long memberId, String nickname, String email, String number);
    Member findById(Long id);

    Member findByUsername(String username);

    Member findByNickname(String nickname);

    void authenticateRegion(Long memberId ,RegionType regionType);

    List<Member> findAll();

}
