package community.independe.domain.post;

import community.independe.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED) // 조인 전략
@DiscriminatorColumn
public abstract class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    @Column(columnDefinition = "text") // 텍스트 타입
    private String content;
}
