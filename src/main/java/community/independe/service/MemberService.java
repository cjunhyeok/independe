package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;
import community.independe.service.dtos.LoginResponse;

import java.util.List;

public interface MemberService {

    Long join(String username, String password, String nickname, String email, String number);
    LoginResponse login(String username, String password, String ip);
    void modifyOAuthMember(Long memberId, String nickname, String email, String number);
    void modifyMember(Long memberId, String username, String password, String nickname, String email, String number);
    void authenticateRegion(Long memberId ,RegionType regionType);
    Member findById(Long id);
    Member findByUsername(String username);
    Member findByNickname(String nickname);
    List<Member> findAll();
}
