package community.independe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    @Id @GeneratedValue
    private Long id;

    private String keywordName;
    private Long keywordCount;

    @Builder
    public Keyword(String keywordName) {
        this.keywordName = keywordName;
        this.keywordCount = 0L;
    }
}
