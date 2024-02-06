package community.independe.api.dtos.member;

import community.independe.service.dtos.ModifyMemberServiceDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModifyMemberRequest {

    @NotEmpty
    private String username;
    @NotEmpty
    @Size(min = 8)
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$")
    @Pattern(regexp = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$",
            message = "숫자, 문자, 특수문자 포함 8~15자리 이내")
    private String password;
    @NotEmpty
    private String nickname;

    // 선택 사항
    @Email
    private String email;
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private String number;

    @Builder
    public ModifyMemberRequest(String username, String password, String nickname, String email, String number) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }

    public static ModifyMemberServiceDto requestToModifyMemberServiceDto(ModifyMemberRequest request, Long memberId) {
        return ModifyMemberServiceDto
                .builder()
                .memberId(memberId)
                .username(request.getUsername())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .number(request.getNumber())
                .build();
    }
}
