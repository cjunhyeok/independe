package community.independe.api.dtos.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageResponse {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String number;

    @Builder
    public MyPageResponse(String username, String password, String nickname, String email, String number) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }
}
