package community.independe.repository.keyword;

import community.independe.domain.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long>, KeywordRepositoryCustom {
}
