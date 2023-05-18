package community.independe.repository;

import community.independe.domain.keyword.Keyword;
import community.independe.domain.keyword.KeywordDto;
import community.independe.repository.keyword.KeywordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
public class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    public void saveTest() {
        // given
        String keywords = "자취";
        String condition = "제목";

        Keyword keyword = Keyword.builder()
                .keyword(keywords)
                .condition(condition)
                .build();

        // when
        Keyword savedKeyword = keywordRepository.save(keyword);

        // then
        assertThat(savedKeyword.getKeyword()).isEqualTo(keywords);
        assertThat(savedKeyword.getCondition()).isEqualTo(condition);
    }

    @Test
    public void groupByTest() {
        // given
        for(int i = 0; i < 5; i++) {
            Keyword keyword = Keyword.builder()
                    .keyword("자취")
                    .condition("제목")
                    .build();
            keywordRepository.save(keyword);
        }
        for (int i = 0; i < 4; i++) {
            Keyword keyword = Keyword.builder()
                    .keyword("생활")
                    .condition("제목")
                    .build();

            keywordRepository.save(keyword);
        }

        // when
        List<KeywordDto> keywordsByGroup = keywordRepository.findKeywordsByGroup();

        // then
        assertThat(keywordsByGroup.size()).isEqualTo(2);
        assertThat(keywordsByGroup.get(0).getKeyword()).isEqualTo("자취");
        assertThat(keywordsByGroup.get(0).getKeywordCount()).isEqualTo(5);
    }
}
