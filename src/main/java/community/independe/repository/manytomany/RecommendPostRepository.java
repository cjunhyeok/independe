package community.independe.repository.manytomany;

import community.independe.domain.manytomany.RecommendPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendPostRepository extends JpaRepository<RecommendPost, Long> {
}
