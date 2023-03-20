package community.independe.repository;

import community.independe.domain.keyword.Keyword;
import community.independe.domain.keyword.KeywordDto;
import community.independe.repository.keyword.KeywordRepository;
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

    @Test
    public void groupByTest() {
        Keyword keyword = new Keyword("자취");
        Keyword keyword2 = new Keyword("자취");
        Keyword keyword3 = new Keyword("생활");
        Keyword keyword4 = new Keyword("생활");
        Keyword keyword5 = new Keyword("생활");
        Keyword keyword6 = new Keyword("꿀팁");

        keywordRepository.save(keyword);
        keywordRepository.save(keyword2);
        keywordRepository.save(keyword3);
        keywordRepository.save(keyword4);
        keywordRepository.save(keyword5);
        keywordRepository.save(keyword6);

        em.flush();
        em.clear();

        List<KeywordDto> keywordsByGroup = keywordRepository.findKeywordsByGroup();

        Assertions.assertThat(keywordsByGroup.get(0).getKeywordName()).isEqualTo("생활");
        Assertions.assertThat(keywordsByGroup.get(0).getKeywordCount()).isEqualTo(3);
    }
}
