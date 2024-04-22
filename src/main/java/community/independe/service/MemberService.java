package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.*;

import java.util.List;

public interface MemberService {

    Long join(JoinServiceDto joinServiceDto);
    LoginResponse login(LoginServiceDto loginServiceDto);
    void modifyOAuthMember(ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto);
    void modifyMember(ModifyMemberServiceDto modifyMemberServiceDto);
    void modifyPassword(Long memberId, String password);
    void authenticateRegion(Long memberId ,RegionType regionType);
    Member findById(Long id);
    Member findByUsername(String username);
    Member findByNickname(String nickname);
    List<Member> findAll();
}
