package community.independe.repository;

import community.independe.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username); // 사용자 id 찾기

}
