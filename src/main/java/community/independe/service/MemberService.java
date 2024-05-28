package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.*;

public interface MemberService {

    Long join(JoinServiceDto joinServiceDto);
    LoginResponse login(LoginServiceDto loginServiceDto);
    void modifyOAuthMember(ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto);
    void modifyMember(ModifyMemberServiceDto modifyMemberServiceDto);
    void modifyPassword(Long memberId, String password);
    void authenticateRegion(Long memberId ,RegionType regionType);
    Member findByNickname(String nickname);
}
