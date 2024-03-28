package community.independe.api.dtos.member;

import community.independe.service.dtos.LoginServiceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty
    @Schema(description = "회원 ID", example = "id1")
    private String username;
    @NotEmpty
    @Size(min = 8)
    @Schema(description = "비밀번호", example = "abc12!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$")
    private String password;

    public static LoginServiceDto loginRequestToLoginServiceDto (LoginRequest request, String ip) {
        return LoginServiceDto
                .builder()
                .username(request.username)
                .password(request.getPassword())
                .ip(ip)
                .build();
    }
}
