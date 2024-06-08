package community.independe.service.integration;

import community.independe.IntegrationTestSupporter;
import community.independe.domain.keyword.Keyword;
import community.independe.domain.keyword.KeywordDto;
import community.independe.repository.keyword.KeywordRepository;
import community.independe.service.KeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
public class KeywordServiceIntegrationTest extends IntegrationTestSupporter {

    @Autowired
    private KeywordService keywordService;
    @Autowired
    private KeywordRepository keywordRepository;

    @Test
    @DisplayName("컨디션과 함께 키워드를 저장한다.")
    void saveKeywordWithConditionTest() {
        // given
        String condition = "title";
        String keyword = "independent";

        // when
        Long savedKeywordId = keywordService.saveKeywordWithCondition(condition, keyword);

        // then
        Keyword findKeyword = keywordRepository.findById(savedKeywordId).get();
        assertThat(findKeyword.getId()).isEqualTo(savedKeywordId);
        assertThat(findKeyword.getConditions()).isEqualTo(condition);
        assertThat(findKeyword.getKeyword()).isEqualTo(keyword);
    }

    @Test
    @DisplayName("키워드를 그룹화해 조회한다.")
    void findKeywordsByGroupTest() {
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
        List<KeywordDto> keywordsByGroup = keywordService.findKeywordsByGroup();

        // then
        assertThat(keywordsByGroup.size()).isEqualTo(2);
    }
}
