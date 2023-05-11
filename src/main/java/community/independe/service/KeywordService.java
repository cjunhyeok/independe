package community.independe.service;

import community.independe.domain.keyword.KeywordDto;

import java.util.List;

public interface KeywordService {

    List<KeywordDto> findKeywordsByGroup();

    Long saveKeywordWithCondition(String condition, String keyword);
}
