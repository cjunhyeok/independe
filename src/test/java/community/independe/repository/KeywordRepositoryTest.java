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
        Keyword keyword = Keyword.builder()
                .keywordName("자취")
                .build();

        Keyword savedKeyword = keywordRepository.save(keyword);

        Assertions.assertThat(savedKeyword.getKeywordName()).isEqualTo("자취");
        Assertions.assertThat(savedKeyword.getKeywordCount()).isEqualTo(0);
    }

    @Test
    public void basicFindByKeywordNameTest() {
        Keyword keyword = Keyword.builder()
                .keywordName("자취")
                .build();

        Keyword savedKeyword = keywordRepository.save(keyword);

        em.flush();
        em.clear();

        Keyword findKeyword = keywordRepository.findByKeywordName("자취");
        Assertions.assertThat(findKeyword.getKeywordName()).isEqualTo("자취");
    }
}
