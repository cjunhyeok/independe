package community.independe.api.dtos.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DuplicateResponse {

    private Boolean idDuplicatedNot;

    public DuplicateResponse(Boolean idDuplicatedNot) {
        this.idDuplicatedNot = idDuplicatedNot;
    }
}
