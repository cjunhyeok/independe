package community.independe.service;

import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.*;

public interface MemberService {

    Long join(JoinServiceDto joinServiceDto);
    LoginResponse login(LoginServiceDto loginServiceDto);
    void modifyOAuthMember(ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto);
    void modifyMember(ModifyMemberServiceDto modifyMemberServiceDto);
    void modifyPassword(Long memberId, String password);
    void authenticateRegion(Long memberId ,RegionType regionType);
    boolean checkDuplicateUsername(String username);
    boolean checkDuplicateNickname(String nickname);
    FindMemberDto findMemberById(Long memberId);
}
