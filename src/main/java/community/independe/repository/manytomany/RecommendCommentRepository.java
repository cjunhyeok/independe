package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendCommentRepository extends JpaRepository<RecommendComment, Long> {
}
