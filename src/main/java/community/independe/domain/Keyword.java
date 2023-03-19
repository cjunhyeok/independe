package community.independe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String keywordName;

    public Keyword(String keywordName) {
        this.keywordName = keywordName;
    }
}
