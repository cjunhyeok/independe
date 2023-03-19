package community.independe.repository;

import community.independe.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Long countAllByKeywordName(String keywordName);

    List<Keyword> findAllByKeywordName(String keywordName);
}
