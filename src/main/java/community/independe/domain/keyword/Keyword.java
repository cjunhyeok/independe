package community.independe.domain.keyword;

import community.independe.domain.BaseEntity;
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
public class Keyword extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String conditions;
    private String keyword;

    @Builder
    public Keyword(String condition, String keyword) {
        this.conditions = condition;
        this.keyword = keyword;
    }
}
