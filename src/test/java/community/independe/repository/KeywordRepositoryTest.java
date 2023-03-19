package community.independe.repository;

import community.independe.domain.Keyword;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    public void basicTest() {
        Keyword keyword = new Keyword("자취");

        Keyword savedKeyword = keywordRepository.save(keyword);

        Assertions.assertThat(savedKeyword.getKeywordName()).isEqualTo("자취");
    }

    @Test
    public void basicFindAllByKeywordNameTest() {
        Keyword keyword = new Keyword("자취");
        Keyword keyword2 = new Keyword("자취");

        Keyword savedKeyword = keywordRepository.save(keyword);
        Keyword savedKeyword2 = keywordRepository.save(keyword2);

        em.flush();
        em.clear();

        List<Keyword> keywords = keywordRepository.findAllByKeywordName("자취");
        Assertions.assertThat(keywords.get(1).getKeywordName()).isEqualTo("자취");
    }

    @Test
    public void countAllByKeywordNameTest() {
        Keyword keyword = new Keyword("자취");
        Keyword keyword2 = new Keyword("자취");

        Keyword savedKeyword = keywordRepository.save(keyword);
        Keyword savedKeyword2 = keywordRepository.save(keyword2);

        em.flush();
        em.clear();

        Long count = keywordRepository.countAllByKeywordName("자취");
        Assertions.assertThat(count).isEqualTo(2);
    }
}
