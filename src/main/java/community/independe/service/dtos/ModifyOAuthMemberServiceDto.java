package community.independe.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ModifyOAuthMemberServiceDto {

    private Long memberId;
    private String nickname;
    private String email;
    private String number;
}
