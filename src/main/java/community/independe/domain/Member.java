package community.independe.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //== 필수 사항 ==//
    @Column(unique = true)
    private String username; // 사용자 Id, 중복금지
    private String password; // 비밀번호
    private String nickname; // 닉네임, 중복금지
    private String role; // 사용자 권한

    //== 선택 사항 == //
    private String email; // 이메일
    private String number; // 전화번호
    @Embedded // 값 타입
    private Address address; // 주소
}
