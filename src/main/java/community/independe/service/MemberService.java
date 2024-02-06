package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.JoinServiceDto;
import community.independe.service.dtos.LoginResponse;
import community.independe.service.dtos.LoginServiceDto;
import community.independe.service.dtos.ModifyOAuthMemberServiceDto;

import java.util.List;

public interface MemberService {

    Long join(JoinServiceDto joinServiceDto);
    LoginResponse login(LoginServiceDto loginServiceDto);
    void modifyOAuthMember(ModifyOAuthMemberServiceDto modifyOAuthMemberServiceDto);
    void modifyMember(Long memberId, String username, String password, String nickname, String email, String number);
    void authenticateRegion(Long memberId ,RegionType regionType);
    Member findById(Long id);
    Member findByUsername(String username);
    Member findByNickname(String nickname);
    List<Member> findAll();
}
