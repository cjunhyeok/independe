package community.independe.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginServiceDto {

    private String username;
    private String password;
    private String ip;
}
