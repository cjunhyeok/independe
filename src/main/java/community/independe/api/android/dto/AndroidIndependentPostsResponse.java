package community.independe.api.android.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AndroidIndependentPostsResponse {

    private Long postId;
    private String nickName;
    private String title;
    private LocalDateTime createdDate;
    private Integer views; // 조회수
    private Long recommendCount; // 추천수
    private Long commentCount; // 댓글수
    private Integer numberOfElements; // 현재 페이지에 나올 데이터 수
    private Boolean hasNextPage; // 다음 페이지 존재 여부
    private Boolean isFirstPage; // 첫 페이지 인지
    private Boolean isLastPage; // 마지막 페이지인지
    private Integer nextPageNumber; // 다음 페이지 넘버
}
