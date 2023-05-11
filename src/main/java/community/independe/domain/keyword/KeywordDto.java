package community.independe.domain.keyword;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeywordDto {

    private String keyword;
    private Long keywordCount;

    public KeywordDto(String keyword, Long keywordCount) {
        this.keyword = keyword;
        this.keywordCount = keywordCount;
    }
}
