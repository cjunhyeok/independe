package community.independe.api.dtos.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DuplicateNicknameRequest {

    private String nickname;
}
