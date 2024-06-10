package community.independe.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FindCommentDto {

    private Long id;
    private String content;
    private Long parentId;
    private Long memberId;
    private LocalDateTime createdDate;

    @Builder
    public FindCommentDto(Long id, String content, Long parentId, Long memberId, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.parentId = parentId;
        this.memberId = memberId;
        this.createdDate = createdDate;
    }
}
