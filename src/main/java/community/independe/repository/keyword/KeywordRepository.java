package community.independe.repository.keyword;

import community.independe.domain.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long>, KeywordRepositoryCustom {

    Long countAllByKeywordName(String keywordName);

    List<Keyword> findAllByKeywordName(String keywordName);

}
