package community.independe.repository;

import community.independe.domain.keyword.KeywordDto;

import java.util.List;

public interface KeywordRepositoryCustom {

    List<KeywordDto> findKeywordsByGroup();
}
