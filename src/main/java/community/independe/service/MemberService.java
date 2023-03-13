package community.independe.service;

import community.independe.domain.member.Member;

public interface MemberService {

    Long join(String username, String password, String nickname, String email, String number, String city, String street, String zipcode);

    Member findById(Long id);
}
