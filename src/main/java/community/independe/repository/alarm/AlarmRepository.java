package community.independe.repository.alarm;

import community.independe.domain.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query(value = "select a from Alarm a left join fetch a.member" +
            " where a.member.id = :memberId" +
            " order by a.createdDate asc",
    countQuery = "select a from Alarm a" +
            " where a.member.id = :memberId" +
            " order by a.createdDate asc")
    List<Alarm> findAllByMemberId(Long memberId);
}
