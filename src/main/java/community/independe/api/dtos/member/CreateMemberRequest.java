package community.independe.api.dtos.member;

import community.independe.service.dtos.JoinServiceDto;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateMemberRequest {

    @AssertTrue
    private Boolean isTermOfUseCheck;
    @AssertTrue
    private Boolean isPrivacyCheck;
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

    public static JoinServiceDto requestToServiceDto(CreateMemberRequest request) {
        return JoinServiceDto
                .builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .number(request.getNumber())
                .isPrivacyCheck(request.getIsPrivacyCheck())
                .isTermOfUseCheck(request.getIsTermOfUseCheck())
                .build();
    }
}
