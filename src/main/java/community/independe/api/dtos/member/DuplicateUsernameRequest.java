package community.independe.api.dtos.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DuplicateUsernameRequest {

    private String username;
}
