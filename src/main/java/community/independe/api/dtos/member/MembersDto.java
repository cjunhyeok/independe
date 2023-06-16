package community.independe.api.dtos.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembersDto {

    private Long memberId;
    private String nickname;
}
