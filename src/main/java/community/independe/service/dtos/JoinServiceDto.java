package community.independe.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JoinServiceDto implements ServiceDto {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String number;
    private Boolean isPrivacyCheck;
    private Boolean isTermOfUseCheck;
}
