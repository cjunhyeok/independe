package community.independe.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class FindMemberDto {

    private String username;
    private String nickname;
    private String email;
    private String number;

    @Builder
    public FindMemberDto(String username, String nickname, String email, String number) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }
}
