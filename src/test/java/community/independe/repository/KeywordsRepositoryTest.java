package community.independe.repository;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.keyword.Keyword;
import community.independe.domain.keyword.KeywordDto;
import community.independe.repository.keyword.KeywordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class KeywordsRepositoryTest extends IntegrationTestSupporter {

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
        Keyword savedKeywords = keywordRepository.save(keyword);

        // then
        assertThat(savedKeywords.getKeyword()).isEqualTo(keywords);
        assertThat(savedKeywords.getConditions()).isEqualTo(condition);
    }

    @Test
    public void groupByTest() {
        // given
        for(int i = 0; i < 5; i++) {
            Keyword keywords = Keyword.builder()
                    .keyword("자취")
                    .condition("제목")
                    .build();
            keywordRepository.save(keywords);
        }
        for (int i = 0; i < 4; i++) {
            Keyword keywords = Keyword.builder()
                    .keyword("생활")
                    .condition("제목")
                    .build();

            keywordRepository.save(keywords);
        }

        // when
        List<KeywordDto> keywordsByGroup = keywordRepository.findKeywordsByGroup();

        // then
        assertThat(keywordsByGroup.size()).isEqualTo(2);
        assertThat(keywordsByGroup.get(0).getKeyword()).isEqualTo("자취");
        assertThat(keywordsByGroup.get(0).getKeywordCount()).isEqualTo(5);
    }
}
