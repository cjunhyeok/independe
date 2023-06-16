package community.independe.api.dtos.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthMemberRequest {

    String nickname;
    String email;
    String number;
}
