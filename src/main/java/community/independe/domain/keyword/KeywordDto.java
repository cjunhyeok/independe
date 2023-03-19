package community.independe.domain.keyword;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeywordDto {

    private String keywordName;
    private Long keywordCount;

    public KeywordDto(String keywordName, Long keywordCount) {
        this.keywordName = keywordName;
        this.keywordCount = keywordCount;
    }
}
