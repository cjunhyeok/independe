package community.independe.service.dtos.main;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MainPostPageRequest {

    private LocalDateTime dateOffset;
    private LocalDateTime dateLimit;
    private int offset;
    private int limit;

    @Builder
    public MainPostPageRequest(LocalDateTime dateOffset, LocalDateTime dateLimit, int offset, int limit) {
        this.dateOffset = dateOffset;
        this.dateLimit = dateLimit;
        this.offset = offset;
        this.limit = limit;
    }

    public void updateLimit(int limit) {
        this.limit = limit;
    }
}
