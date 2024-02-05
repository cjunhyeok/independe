package community.independe.api.dtos.member;

import community.independe.service.dtos.LoginServiceDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty
    private String username;
    @NotEmpty
    @Size(min = 8)
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
