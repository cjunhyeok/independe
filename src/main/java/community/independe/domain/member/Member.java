package community.independe.domain.member;

import community.independe.domain.BaseEntity;
import community.independe.domain.post.enums.RegionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Boolean isTermOfUseCheck; // 약관 동의
    private Boolean isPrivacyCheck; // 개인정보 동의

    //== 선택 사항 == //
    private String number; // 전화번호
    @Enumerated(EnumType.STRING)
    private RegionType region; // 지역

    //== Oauth2 ==//
    private String registrationId; // 인가서버 식별자
    private String provider; // 인가서버
    private String email; // 이메일

    //== 생성 메서드 ==//
    @Builder
    public Member(String username, String password, String nickname, String role, Boolean isTermOfUseCheck, Boolean isPrivacyCheck,
                               String email, String number, RegionType region,
                               String registrationId, String provider) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.isTermOfUseCheck = isTermOfUseCheck;
        this.isPrivacyCheck = isPrivacyCheck;
        this.email = email;
        this.number = number;
        this.region = region;
        this.registrationId = registrationId;
        this.provider = provider;
    }

    // 위치 인증 시 변경 메서드
    public void authenticateRegion(RegionType region) {
        this.region = region;
    }

    // 소셜 로그인을 위한 수정
    public void oauthMember(String nickname, String email, String number) {
        this.nickname = nickname;
        this.email = email;
        this.number = number;
        this.role = "ROLE_USER";
    }

    // 회원 정보 수정
    public void modifyMember(String nickname, String email, String number) {
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }

    // 비밀번호 수정
    public void modifyPassword(String password) {
        this.password = password;
    }
}
