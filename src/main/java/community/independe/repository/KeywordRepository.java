package community.independe.repository;

import community.independe.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Long countAllByKeywordCount(String keywordCount);

    Keyword findByKeywordName(String keywordName);
}
