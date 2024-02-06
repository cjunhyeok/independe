package community.independe.api.dtos.member;

import community.independe.service.dtos.ModifyOAuthMemberServiceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthMemberRequest {

    private String nickname;
    private String email;
    private String number;

    public static ModifyOAuthMemberServiceDto requestToModifyOAuthMemberServiceDto(OAuthMemberRequest request, Long memberId) {
        return ModifyOAuthMemberServiceDto
                .builder()
                .memberId(memberId)
                .nickname(request.nickname)
                .number(request.number)
                .email(request.email)
                .build();
    }
}
