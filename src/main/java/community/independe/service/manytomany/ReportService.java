package community.independe.service.manytomany;

import community.independe.domain.manytomany.ReportPost;

public interface ReportService {

    Long save(Long postId, Long memberId);

    ReportPost findByPostIdAndMemberId(Long postId, Long memberId);

    ReportPost findByPostIdAndMemberIdAndIsRecommend(Long postId, Long memberId);
}
