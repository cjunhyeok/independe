package community.independe.service;

import community.independe.domain.keyword.Keyword;
import community.independe.domain.keyword.KeywordDto;
import community.independe.repository.keyword.KeywordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeywordServiceTest {

    @InjectMocks
    private KeywordServiceImpl keywordService;
    @Mock
    private KeywordRepository keywordRepository;

    @Test
    public void saveKeywordWithConditionTest() {
        // given
        String keyword = "자취";
        String condition = "제목";

        // stub
        when(keywordRepository.save(any(Keyword.class))).thenReturn(Keyword.builder().build());

        // when
        keywordService.saveKeywordWithCondition(condition, keyword);

        // then
        verify(keywordRepository, times(1)).save(any(Keyword.class));
    }

    @Test
    void findKeywordsByGroupTest() {
        // given
        List<KeywordDto> keywords = new ArrayList<>();
        keywords.add(new KeywordDto());

        // stub
        when(keywordRepository.findKeywordsByGroup()).thenReturn(keywords);

        // when
        List<KeywordDto> findKeywords = keywordService.findKeywordsByGroup();

        // then
        assertThat(findKeywords).isEqualTo(keywords);
        verify(keywordRepository, times(1)).findKeywordsByGroup();
    }
}
