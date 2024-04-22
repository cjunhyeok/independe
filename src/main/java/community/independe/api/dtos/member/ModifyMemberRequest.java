package community.independe.api.dtos.member;

import community.independe.service.dtos.ModifyMemberServiceDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifyMemberRequest {

    @NotEmpty
    private String nickname;
    // 선택 사항
    @Email
    private String email;
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private String number;

    @Builder
    public ModifyMemberRequest(String nickname, String email, String number) {
        this.nickname = nickname;
        this.email = email;
        this.number = number;
    }

    public static ModifyMemberServiceDto requestToModifyMemberServiceDto(ModifyMemberRequest request, Long memberId) {
        return ModifyMemberServiceDto
                .builder()
                .memberId(memberId)
                .nickname(request.getNickname())
                .email(request.getEmail())
                .number(request.getNumber())
                .build();
    }
}
