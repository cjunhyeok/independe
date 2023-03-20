package community.independe.repository.keyword;

import community.independe.domain.keyword.KeywordDto;

import java.util.List;

public interface KeywordRepositoryCustom {

    List<KeywordDto> findKeywordsByGroup();
}
