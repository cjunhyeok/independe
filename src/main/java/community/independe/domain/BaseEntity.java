package community.independe.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // auditing
public class BaseEntity {

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate; // 생성 일자

    @LastModifiedDate
    private LocalDateTime lastModifiedDate; // 수정 일자

//    @PrePersist // persist 전에 발생
//    public void prePersist() {
//        LocalDateTime now = LocalDateTime.now();
//        createdDate = now;
//        lastModifiedDate = now;
//    }
//
//    @PreUpdate // update 전에 발생
//    public void preUpdate() {
//        lastModifiedDate = LocalDateTime.now();
//    }
}
