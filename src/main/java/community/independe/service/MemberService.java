package community.independe.service;

import community.independe.domain.member.Member;
import community.independe.domain.post.enums.RegionType;

public interface MemberService {

    Long join(String username, String password, String nickname, String email, String number, String city, String street, String zipcode);

    Member findById(Long id);

    Member findByUsername(String username);

    Member findByNickname(String nickname);

    void authenticateRegion(Long memberId ,RegionType regionType);
}
